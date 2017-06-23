package html

/**
 * Created by paul on 6/23/17.
 */

fun main(args: Array<String>) {
    val page = html {
        head { +"This is header"
            title { +"Title"}
        }
        body {
            h1 { +"Header "}
        }
    }

    println(page)
}

interface IElement {
    fun render(b :StringBuilder, indent :String)
}

class TextElement(val text:String) : IElement {
    override fun render(b: StringBuilder, indent: String) {
        b.append("$indent$text\n");
    }
}

abstract class Tag(val name: String) : IElement {
    val attributes = hashMapOf<String, String>()
    val children = arrayListOf<IElement>()

    protected fun render(b: StringBuilder) = render(b, "")

    override fun render(b: StringBuilder, indent: String) {
        // render open tag and its enclosed attributes
        b.append("$indent<$name")
        attributes.entries.forEach { (key, value) -> b.append(" ").append(key).append("=").append(value) }
        b.append(">\n")

        // render all children element
        val newIndent = indent + "  "
        children.forEach() { it.render(b, newIndent) }

        // render ending tag
        b.append("$indent</$name>\n")
    }

    protected fun <T : IElement> addChildElement(child: T, initLambda: T.() -> Unit) {
        children.add(child)
        child.initLambda()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder)
        return builder.toString()
    }
}

abstract class TagWithText(name:String) : Tag(name) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

//-------------------

class HTML : Tag("html") {
    fun head( initLambda: Head.()->Unit) = addChildElement( Head(), initLambda )
    fun body( initLambda: Body.()->Unit) = addChildElement( Body(), initLambda)
}

class Head : TagWithText("head") {
    fun title( initLambda: Title.()->Unit) = addChildElement(Title(), initLambda)
}

class Body : TagWithText("body") {
    fun h1( initLambda : H1.()->Unit ) = addChildElement( H1(), initLambda)
    fun p( initLambda : P.()->Unit ) = addChildElement( P(), initLambda)
    fun b(initLambda: B.() -> Unit) = addChildElement(B(), initLambda)
}

class Title : TagWithText("title")

class H1 : TagWithText("h1")

class B : TagWithText("b")

class P : TagWithText("p")

fun html( initLambda : HTML.()->Unit) : HTML {
    val html = HTML()
    html.initLambda()
    return html
}