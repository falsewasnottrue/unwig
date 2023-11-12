package de.falsewasnottrue.file

import java.io.File
import java.io.RandomAccessFile

class SeekableFile(file: File) {
    private val raf: RandomAccessFile = RandomAccessFile(file, "rw")

    fun position(): Long {
        return raf.filePointer
    }

    fun read(): Int {
        return raf.read()
    }

    fun readDouble(): Double {
        return try {
            val data = ByteArray(8)
            raf.read(data)
            readDouble(data, 0, 8)
        } catch (e: Exception) {
            0.0
        }
    }

    fun readFully(buf: ByteArray?) {
        raf.read(buf)
    }

    fun readInt(): Int {
        return try {
            val data = ByteArray(4)
            raf.read(data)
            readInt(data, 0, 4)
        } catch (e: Exception) {
            0
        }
    }

    fun readLong(): Long {
        val buffer = ByteArray(8)
        raf.read(buffer)
        return readLong(buffer, 0, 8)
    }

    fun readShort(): Short {
        val r = ByteArray(2)
        raf.read(r)
        return (r[1].toInt() shl 8 or (r[0].toInt() and 0xff)).toShort()
    }

    fun readString(): String {
        val sb = StringBuilder()
        var b = raf.read()
        while (b > 0) {
            sb.append(b.toChar())
            b = raf.read()
        }
        return sb.toString()
    }

    fun seek(pos: Long) {
        raf.seek(pos)
    }

    fun skip(what: Long): Long {
        return raf.skipBytes(what.toInt()).toLong()
    }

    companion object {
        private const val TAG = "WSeekableFile"

        private fun readDouble(buffer: ByteArray, start: Int, len: Int): Double {
            var result: Long = 0
            for (i in 0 until len) {
                result = result or ((buffer[start + i].toInt() and 0xff).toLong() shl i * 8)
            }
            return java.lang.Double.longBitsToDouble(result)
        }

        private fun readInt(buffer: ByteArray, start: Int, len: Int): Int {
            var result = 0
            for (i in 0 until len) {
                result += buffer[start + i].toInt() and 0xFF shl i * 8
            }
            return result
        }

        private fun readLong(buffer: ByteArray, start: Int, len: Int): Long {
            var result: Long = 0
            for (i in 0 until len) {
                result = result or ((buffer[start + i].toInt() and 0xff).toLong() shl i * 8)
            }
            return result
        }
    }
}
