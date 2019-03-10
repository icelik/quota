package icelik.quota.apigw.kafka.listener.filter;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

public class FilterOutOfDateRecordFilterStrategy implements RecordFilterStrategy<String, String> {
	@Override
	public boolean filter(ConsumerRecord<String, String> consumerRecord) {
		return Long.valueOf(consumerRecord.value()) < System.currentTimeMillis();
	}
}
