package com.erhun.framework.orm.version;

import org.redisson.Redisson;
import org.redisson.api.RMap;

import java.util.Map;

/**
 * 版本管理器
 * 
 * @author Birdy
 *
 */
// TODO 很重要,需要考虑万一Redis宕机情况下的容灾方案.
public class VersionManager {

	public final static String VERSION_PREFIX = "version_";

	/** Redis */
	private Redisson redisson;

	public VersionManager(Redisson redisson) {
		this.redisson = redisson;
	}

	/**
	 * 根据用户更新版本
	 * 
	 * @param userId
	 * @param clazz
	 * @return
	 */
	public long updateVersion(long userId, Class<?> clazz) {
		return updateVersion(userId, clazz, 1);
	}

	/**
	 * 根据用户获取版本
	 * 
	 * @param userId
	 * @return
	 */
	public Map<String, Number> getVersions(long userId) {
		RMap<String, Number> versions = redisson.getMap(VERSION_PREFIX + String.valueOf(userId));
		return versions.readAllMap();
	}

	/**
	 * 根据用户更新版本(仅迁移数据用)
	 * 
	 * @param userId
	 * @param clazz
	 * @param number
	 * @return
	 */
	public long updateVersion(long userId, Class<?> clazz, int number) {
		assert number > 0;
		RMap<String, Number> versions = redisson.getMap(VERSION_PREFIX + String.valueOf(userId));
		Number version = versions.addAndGet(clazz.getSimpleName(), number);
		return version.longValue();
	}

	/**
	 * 获取用户更新版本
	 * 
	 * @param userId
	 * @param clazz
	 * @return
	 */
	public long getVersion(long userId, Class<?> clazz) {
		RMap<String, Number> versions = redisson.getMap(VERSION_PREFIX + String.valueOf(userId));
		Number version = versions.getOrDefault(clazz.getSimpleName(), Long.valueOf(0L));
		return version.longValue();
	}

}
