package com.luck.picture.lib.factory

import android.view.View

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：class factory
 */
class ClassFactory {
    class NewInstance : Factory {
        override fun <Model> create(targetClass: Class<Model>): Model {
            try {
                return targetClass.newInstance()
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $targetClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $targetClass", e)
            }
        }
    }

    class NewConstructorInstance : ConstructorFactory {
        override fun <Model> create(targetClass: Class<Model>, view: View): Model {
            try {
                val constructor = targetClass.getDeclaredConstructor(View::class.java)
                constructor.isAccessible = true
                return constructor.newInstance(view)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $targetClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $targetClass", e)
            }
        }

    }

    private interface Factory {
        fun <Model> create(targetClass: Class<Model>): Model
    }

    private interface ConstructorFactory {
        fun <Model> create(targetClass: Class<Model>, view: View): Model
    }
}