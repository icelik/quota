package icelik.quota.service.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class QuotaStreamsConfig {
	private static final String STORE_NAME = "minute-window-store";
	@Value("${block.time.in.milis:60000}")
	private long blockTimeInMilis;

	@Value("${block.treshold:10}")
	private long blockingTreshold;

	@Bean
	public KStream<String, String> kStream(StreamsBuilder kStreamBuilder) {
		KStream<String, String> requests = kStreamBuilder
				.stream("requests", Consumed.with(Serdes.String(), Serdes.String()));
		requests
				.groupByKey()
				.windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
				.count(Materialized.as(STORE_NAME))
				.toStream((key, value) -> key.key())
				.filter((key, value) -> value > (blockingTreshold - 1))
				.mapValues(value -> System.currentTimeMillis() + blockTimeInMilis)
				.to("blocked");
		return requests;
	}
}
