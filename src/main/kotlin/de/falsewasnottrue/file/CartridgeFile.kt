package de.falsewasnottrue.file

class CartridgeFile(private val source: SeekableFile) {

    // TODO protected var savegame: Savegame? = null
    var files: Short = 0

    // TODO offsets -> move to map
    var offsets: IntArray = IntArray(0)
    var ids: ShortArray = ShortArray(0)

    // Header value
    // TODO move to value class
    var latitude = 0.0
    var longitude = 0.0
    var type: String? = null
    var member: String? = null
    var name: String? = null
    var description: String? = null
    var startdesc: String? = null
    var version: String? = null
    var author: String? = null
    var url: String? = null
    var device: String? = null
    var code: String? = null
    var iconId: Short = 0
    var splashId: Short = 0

    var filename: String? = null

    private fun fileOk(): Boolean {
        val buf = ByteArray(CART_ID.size)
        source.seek(0)
        source.readFully(buf)
        for (i in buf.indices) if (buf[i] != CART_ID[i]) return false
        return true
    }

    // TODO refactor to return map of offsets
    private fun scanOffsets() {
        files = source.readShort()
        offsets = IntArray(files.toInt())
        ids = ShortArray(files.toInt())
        for (i in 0 until files.toInt()) {
            ids[i] = source.readShort()
            offsets[i] = source.readInt()
        }
    }

    private fun scanHeader() {
        source.readInt() // header length
        latitude = source.readDouble()
        longitude = source.readDouble()
        source.skip(8) // zeroes
        source.skip(4 + 4) // unknown long values
        splashId = source.readShort()
        iconId = source.readShort()
        type = source.readString()
        member = source.readString()
        source.skip(4 + 4) // unknown long values
        name = source.readString()
        source.readString() // GUID
        description = source.readString()
        startdesc = source.readString()
        version = source.readString()
        author = source.readString()
        url = source.readString()
        device = source.readString()
        source.skip(4) // unknown long value
        code = source.readString()
    }

    val bytecode: ByteArray
        /** Return the Lua bytecode for this cartridge.  */
        get() {
            source.seek(offsets[0].toLong())
            val len: Int = source.readInt()
            val ffile = ByteArray(len)
            source.readFully(ffile)
            return ffile
        }

    private var lastId = -1
    private var lastFile: ByteArray? = null

    /** Return data of the specified data file.  */
    fun getFile(oid: Int): ByteArray? {
        if (oid == lastId) return lastFile
        if (oid < 1) // invalid, apparently. or bytecode - lookie no touchie
            return null
        var id = -1
        for (i in ids.indices) if (ids[i].toInt() == oid) {
            id = i
            break
        }
        if (id == -1) return null
        source.seek(offsets[id].toLong())
        val a: Int = source.read()

        // id of resource. 0 means deleted
        if (a < 1) return null
        val ttype: Int = source.readInt() // we don't need this?
        val len: Int = source.readInt()

        // we found the data - release cache
        lastFile = null
        lastId = -1
        val ffile: ByteArray
        try {
            ffile = ByteArray(len)
            source.readFully(ffile)
        } catch (e: OutOfMemoryError) {
            return null
        }
        if (len < CACHE_LIMIT) {
            lastId = oid
            lastFile = ffile
        }
        return ffile
    }

    // TODO fix
//    @Throws(IOException::class)
//    fun getSavegame(): Savegame? {
//        return savegame
//    }

    companion object {
        // 02 0a CART 00
        private val CART_ID = byteArrayOf(0x02, 0x0a, 0x43, 0x41, 0x52, 0x54, 0x00)

        private const val CACHE_LIMIT = 128000 // in kB

        /** Read the specified file and return a corresponding CartridgeFile object.
         *
         * @param source file representing the cartridge
         * @param savefile save file corresponding to this cartridge
         * @return a CartridgeFile object corresponding to source
         * @throws IOException
         */
        fun read(source: SeekableFile /*, savefile: FileHandle?*/): CartridgeFile? {
            val cf = CartridgeFile(source)

            if (!cf.fileOk()) {
                println("Wrong file type")
                return null
            } else {
                cf.scanOffsets()
                cf.scanHeader()

                // TODO
                // cf.savegame = Savegame(savefile)
                return cf

            }
        }
    }
}
