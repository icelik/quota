package icelik.quota.service;

public class Count {
	private long total = 0L;
	private long treshold;
	private long blockDuration;

	public Count increment() {
		this.total++;
		return this;
	}

	public long getTotal() {
		return total;
	}


	public long getTreshold() {
		return treshold;
	}

	public void setTreshold(Long treshold) {
		this.treshold = treshold;
	}

	public long getBlockDuration() {
		return blockDuration;
	}

	public void setBlockDuration(Long blockDuration) {
		this.blockDuration = blockDuration;
	}

	public boolean isExceedTreshold() {
		return this.total > (this.treshold - 1);
	}
}
