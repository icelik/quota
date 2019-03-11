package icelik.quota.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import icelik.quota.service.Count;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Configuration
public class QuotaStreamsConfig {
	private static final String STORE_NAME = "minute-window-store";
	private static ObjectMapper objectMapper = new ObjectMapper();


	private Aggregator<String, String, Count> aggregator = (key, value, aggregate) -> {
		try {
			Map limitHolderAsMap = objectMapper.readValue(value, Map.class);
			aggregate.setBlockDuration(((Number) limitHolderAsMap.get("blockDuration")).longValue());
			aggregate.setTreshold(((Number) limitHolderAsMap.get("treshold")).longValue());
			aggregate.increment();
			return aggregate;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	};


	@Bean
	public KStream<String, String> kStream(StreamsBuilder kStreamBuilder) {

		Materialized<String, Count, WindowStore<Bytes, byte[]>> materialized = Materialized.as(STORE_NAME);
		materialized.withKeySerde(Serdes.String());
		materialized.withValueSerde(new JsonSerde<>(Count.class));

		KStream<String, String> requests = kStreamBuilder
				.stream("requests", Consumed.with(Serdes.String(), Serdes.String()));


		requests
				.filter(this::isValid)
				.groupByKey()
				.windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
				.aggregate(
						Count::new,
						aggregator
						, materialized
				)
				.toStream((key, value) -> key.key())
				.filter((key, value) -> value.isExceedTreshold())
				.mapValues(value -> System.currentTimeMillis() + value.getBlockDuration())
				.to("blocked");

		return requests;
	}

	private boolean isValid(String key, String value) {

		if (StringUtils.isEmpty(key))
			return false;

		try {
			Map limitHolderAsMap = objectMapper.readValue(value, Map.class);

			if (limitHolderAsMap.get("treshold") == null)
				return false;

			if (limitHolderAsMap.get("blockDuration") == null)
				return false;

			return true;

		} catch (IOException e) {
			return false;
		}
	}
}
