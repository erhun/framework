package com.erhun.framework.orm.identity;

import org.redisson.Redisson;
import org.redisson.api.RMap;

import java.util.HashMap;
import java.util.List;

/**
 * 标识管理器
 * 
 * @author Birdy
 *
 */
// TODO 很重要,需要考虑万一Redis宕机情况下的容灾方案.
public class IdentityManager {

	public final static String IDENTITY_PREFIX = "identity_";

	/** 分段大小 */
	private int size;

	/** 标识定义 */
	private IdentityDefinition definition;

	/** 标识工厂 */
	private HashMap<Class<?>, RedisIdentityFactory[]> factories;

	private Redisson redisson;

	private long step;

	public IdentityManager(IdentityDefinition definition, Redisson redisson, long step) {
		List<IdentitySection> sections = definition.getSections();
		assert sections.size() == 2;
		this.definition = definition;
		this.size = 1 << sections.get(0).getBit();
		this.factories = new HashMap<>();
		this.redisson = redisson;
		this.step = step;
	}

	private RedisIdentityFactory[] getFactories(Class<?> clazz) {
		RedisIdentityFactory[] factories = this.factories.get(clazz);
		if (factories == null) {
			synchronized (clazz) {
				RMap<Integer, Number> map = redisson.getMap(IDENTITY_PREFIX + clazz.getSimpleName());
				factories = this.factories.get(clazz);
				if (factories == null) {
					factories = new RedisIdentityFactory[size];
					for (int index = 0; index < size; index++) {
						RedisIdentityFactory factory = new RedisMapIdentityFactory(map, definition, index, step);
						factories[index] = factory;
					}
					this.factories.put(clazz, factories);
				}
			}
		}
		return factories;
	}

	/**
	 * 根据分段索引获取分段标识
	 * 
	 * @param index
	 * @return
	 */
	public long getSectionId(Class clazz, int index) {
		RedisIdentityFactory[] factories = getFactories(clazz);
		return factories[index].getSequence();
	}

	/**
	 * 根据分段标识获取分段索引
	 * 
	 * @param id
	 * @return
	 */
	public int getSectionIndex(long id) {
		long[] sections = definition.parse(id);
		return (int) sections[0];
	}

	/**
	 * 获取分段大小
	 * 
	 * @return
	 */
	public int getSectionSize() {
		return size;
	}

}
