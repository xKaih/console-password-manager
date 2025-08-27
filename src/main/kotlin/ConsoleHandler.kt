package io.github.xkaih
//Btw this is the Console Handler from my other project https://github.com/xKaih/console-task-manager
//It has been improved using object instead of class and inside a companion object

//For the moment I have no idea how enum classes works, but ChatGPT does so... Who cares (this is not like in C# lol)
enum class AnsiColor(val code: String) {
    //Normal colors
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),

    // Bright colors
    BRIGHT_BLACK("\u001B[90m"),
    BRIGHT_RED("\u001B[91m"),
    BRIGHT_GREEN("\u001B[92m"),
    BRIGHT_YELLOW("\u001B[93m"),
    BRIGHT_BLUE("\u001B[94m"),
    BRIGHT_PURPLE("\u001B[95m"),
    BRIGHT_CYAN("\u001B[96m"),
    BRIGHT_WHITE("\u001B[97m")
}

//Probably there is some library for console stuffs in Kotlin or Java but, it is funny to try to do one myself :D. Even if I'm just making it has simplest has possible
//UPDATE: Yeah, there is a library called jline3, maybe I will read the source code of it, seems interesting (probably I will procrastinate and won't read it LOL)
object ConsoleHandler {
    private fun colorize(text: String, color: AnsiColor): String {
        return "${color.code}${text}"
    }

    //I just asked ChatGPT for this... Idk why google does this things complex. Maybe by the fact that kotlin is not meant for console apps LOL
    private fun getTerminalWidth(): Int {
        return try {
            val os = System.getProperty("os.name").lowercase()
            val command =
                if (os.contains("windows")) arrayOf("powershell", "-Command", "(Get-Host).UI.RawUI.WindowSize.Width")
                else arrayOf("sh", "-c", "tput cols")
            val process = ProcessBuilder(*command).start()
            process.inputStream.bufferedReader().readLine().toInt()
        } catch (e: Exception) {
            80 // fallback
        }
    }

    fun readLineInto(setter: (String?) -> Unit): ConsoleHandler {
        val input = readln()
        setter(input)
        return this
    }

    //Anyway, this doesn't work properly on IDEs console so, I won't use it
    private fun printCentered(text: String, line: Boolean) {
        val consoleWidth = getTerminalWidth()
        val spacesToCenter =
            (consoleWidth - text.length) / 2 //Maybe I should add a validation on the case that the text is too large for the console...
        repeat(spacesToCenter) {
            println(" ")
        }
        if (line)
            println(text)
        else
            print(text)
    }

    fun printlnWithColor(text: String, color: AnsiColor, horizontalCentered: Boolean = false): ConsoleHandler {
        val textColorized = colorize(text, color)

        if (horizontalCentered)
            printCentered(textColorized, true)
        else
            println(textColorized)

        return this
    }

    fun lineBreak(): ConsoleHandler {
        println()
        return this
    }

    fun printWithColor(text: String, color: AnsiColor, horizontalCentered: Boolean = false): ConsoleHandler {
        val textColorized = colorize(text, color)

        if (horizontalCentered)
            printCentered(textColorized, false)
        else
            print(textColorized)

        return this
    }


    //Btw, this is obviously not a real clear cause this language cant handle with that LOL. (Why im doing this with KOT and not C# 😭. Omg, all for my studies 🤡)
    //I am just keeping it like this while it not causes problems to me.
    fun clearConsole() {
        repeat(50) {
            println()
        }
    }

}