package com.luck.picture.lib.obj.pool;

import java.util.LinkedList;

/**
 * @author：luck
 * @date：2022/6/25 22:36 晚上
 * @describe：ObjectPools
 */
public final class ObjectPools {

    public ObjectPools() {
    }

    public interface Pool<T> {
        /**
         * 获取对象
         */
        T acquire();

        /**
         * 释放对象
         */
        boolean release(T obj);

        /**
         * 销毁对象池
         */
        void destroy();
    }

    public static class SimpleObjectPool<T> implements Pool<T> {
        private final LinkedList<T> mPool;

        public SimpleObjectPool() {
            mPool = new LinkedList<>();
        }

        @Override
        public T acquire() {
            return mPool.poll();
        }

        @Override
        public boolean release(T obj) {
            if (isInPool(obj)){
                return false;
            }
            return mPool.add(obj);
        }

        @Override
        public void destroy() {
            mPool.clear();
        }

        private boolean isInPool(T obj) {
            return mPool.contains(obj);
        }
    }


    public static class SynchronizedPool<T> extends SimpleObjectPool<T> {
        private final Object mLock = new Object();

        @Override
        public T acquire() {
            synchronized (mLock) {
                return super.acquire();
            }
        }

        @Override
        public boolean release(T obj) {
            synchronized (mLock) {
                return super.release(obj);
            }
        }

        @Override
        public void destroy() {
            synchronized (mLock) {
                super.destroy();
            }
        }
    }
}
