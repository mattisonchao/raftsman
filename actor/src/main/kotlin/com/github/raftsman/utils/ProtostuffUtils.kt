package com.github.raftsman.utils

import io.protostuff.LinkedBuffer
import io.protostuff.ProtobufIOUtil
import io.protostuff.Schema
import io.protostuff.runtime.RuntimeSchema
import java.util.*
import java.util.concurrent.ConcurrentHashMap


object ProtostuffUtils {

    private val schemaCache = ConcurrentHashMap<Class<*>, Schema<*>>()

    fun <T : Any> serialize(obj: T): ByteArray {
        val schema: Schema<T> = getSchema(obj.javaClass)
        val buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE)
        return try {
            ProtobufIOUtil.toByteArray(obj, schema, buffer)
        } finally {
            buffer.clear()
        }
    }

    fun <T : Any> deserialize(data: ByteArray, clazz: Class<T>): T {
        val schema: Schema<T> = getSchema(clazz)
        val obj = schema.newMessage()
        ProtobufIOUtil.mergeFrom(data, obj, schema)
        return obj
    }

    private fun <T : Any> getSchema(clazz: Class<T>): Schema<T> {
        val schema = schemaCache[clazz]
        if (Objects.isNull(schema)) {
            val newSchema = RuntimeSchema.getSchema(clazz) as Schema<T>
            schemaCache[clazz] = newSchema
            return newSchema
        }
        @Suppress("UNCHECKED_CAST")
        return schema as Schema<T>
    }
}