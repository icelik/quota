package icelik.quota.apigw.kafka.listener.filter;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

public class FilterOutOfDateRecordFilterStrategy implements RecordFilterStrategy<String, Long> {
	@Override
	public boolean filter(ConsumerRecord<String, Long> consumerRecord) {

		if (consumerRecord == null)
			return true;

		return consumerRecord.value() < System.currentTimeMillis();
	}
}
