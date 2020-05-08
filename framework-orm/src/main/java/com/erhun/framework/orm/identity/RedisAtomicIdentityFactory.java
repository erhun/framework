package com.erhun.framework.orm.identity;

import org.redisson.api.RAtomicLong;

public class RedisAtomicIdentityFactory extends RedisIdentityFactory {

	private RAtomicLong redisson;

	public RedisAtomicIdentityFactory(RAtomicLong redisson, IdentityDefinition definition, int partition, long step) {
		super(definition, partition, step);
		this.redisson = redisson;
	}

	@Override
	protected long getLimit(long step) {
		return redisson.addAndGet(step);
	}

}
