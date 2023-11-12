import de.falsewasnottrue.file.CartridgeFile
import de.falsewasnottrue.file.SeekableFile
import se.krka.kahlua.stdlib.*
import se.krka.kahlua.vm.LuaPrototype
import se.krka.kahlua.vm.LuaState
import se.krka.kahlua.vm.LuaTableImpl
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

fun main(args: Array<String>) {

    fun prepareState(): LuaState {
        val state = LuaState(System.out)

        println("Registering base libs...");
		BaseLib.register(state);
		MathLib.register(state);
		StringLib.register(state);
		CoroutineLib.register(state);
		OsLib.register(state);

        println("Loading stdlib...")
        val stdlib: InputStream = CartridgeFile::class.java.getResourceAsStream("/stdlib.lbc")
        val closure = LuaPrototype.loadByteCode(stdlib, state.environment)

        state.call(closure, null, null, null)

        return state
    }

    val filename = "/parkabenteuer.gwc"
    val url = CartridgeFile::class.java.getResource(filename)
    val file = File(url.file)
    val source = SeekableFile(file)

    val cf = CartridgeFile.read(source)

    if (cf != null) {
        println("author ${cf.author}")

        println("latitude ${cf.latitude}")
        println("longitude ${cf.longitude}")
        println("type ${cf.type}")
        println("member ${cf.member}")
        println("name ${cf.name}")
//        println("description ${cf.description}")
        println("startdesc ${cf.startdesc}")
        println("version ${cf.version}")
        println("author ${cf.author}")
        println("url ${cf.url}")
        println("device ${cf.device}")
        println("code ${cf.code}")
        println("iconId ${cf.iconId}")
        println("splashId ${cf.splashId}")

//        println("ids:")
//        cf.ids.forEach { print("$it, ") }

//        println("offsets:")
//        cf.offsets.forEach { print("$it, ") }

        println("bytecode")
        val bytecode = cf.bytecode
        println("  length: ${bytecode.size}")

        val state = prepareState()
        val stream = ByteArrayInputStream(bytecode)
        val closure = LuaPrototype.loadByteCode(stream, state.environment)

        println("done")
    }
}

