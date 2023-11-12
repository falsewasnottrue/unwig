import de.falsewasnottrue.CartridgeFile
import de.falsewasnottrue.SeekableFile
import java.io.File

fun main(args: Array<String>) {

    val filename = "/parkabenteuer.gwc"
    val url = CartridgeFile::class.java.getResource(filename)
    val file = File(url.file)
    val source = SeekableFile(file)

    val cf = CartridgeFile.read(source)

    if (cf != null) {
        println(cf.author)

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

        println("ids:")
        cf.ids.forEach { print("$it, ") }

        println("offsets:")
        cf.offsets.forEach { print("$it, ") }
    }

}

