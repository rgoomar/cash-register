package cashregister

import jline.console.ConsoleReader
import jline.console.completer.FileNameCompleter
import jline.console.completer.StringsCompleter
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.PrintWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val bills: List<Bill> = listOf(
            Bill(20),
            Bill(10),
            Bill(5),
            Bill(2),
            Bill(1)
    )
    val register: Register = Register(bills)
    val inputStream = FileInputStream(FileDescriptor.`in`)
    val reader = ConsoleReader("App", inputStream, System.out, null)

    reader.prompt = "> "

    reader.addCompleter(FileNameCompleter())
    reader.addCompleter(StringsCompleter(listOf("show", "put", "take", "change", "quit")))
    val out = PrintWriter(reader.output)

    var line: String?
    line = reader.readLine()
    while (line != null) {
        when {
            line.startsWith("show") -> showRegister(out, register)
            line.startsWith("take") -> takeRegister(out, register, line)
            line.startsWith("put") -> addToRegister(out, register, line)
            line.startsWith("change") -> getChangeFromRegister(out, register, line)
            line.startsWith("quit") -> exitProcess(0)
            else -> help(out)
        }
        line = reader.readLine()
    }
    reader.flush()
}

fun help(out: PrintWriter) {
    out.println("Available Commands")
    out.println("show - Shows the current amount in register")
    out.println("take [$20] [$10] [$5] [$2] [$1] - Take money from the register")
    out.println("put [$20] [$10] [$5] [$2] [$1] - Put money from the register")
    out.println("change [amount] - Get change from the register")
    out.println("------------------")
}

fun findQuantities(line: String): List<Int> {
    val cmd = line.split(" ")
    return cmd.subList(1, cmd.size).map(String::toInt)
}

fun showRegister(out: PrintWriter, register: Register) {
    out.println("\$${register.getTotal()} ${register.showQuantities()}")
}

fun takeRegister(out: PrintWriter, register: Register, line: String) {
    val quantities = findQuantities(line)
    val bills = register.bills
    if (quantities.size != bills.size) {
        out.println(quantities.toString())
        out.println("Invalid command")
        help(out)
    } else {
        register.removeQuantities(quantities)
        showRegister(out, register)
    }
}

fun addToRegister(out: PrintWriter, register: Register, line: String) {
    val quantities = findQuantities(line)
    val bills = register.bills
    if (quantities.size != bills.size) {
        out.println(quantities.toString())
        out.println("Invalid command")
        help(out)
    } else {
        register.addQuantities(quantities)
        showRegister(out, register)
    }
}

fun getChangeFromRegister(out: PrintWriter, register: Register, line: String) {
    val cmd = line.split(" ")
    val changeRequested: Int = cmd[1].toInt()
    try {
        val changeBack: List<Int> = register.sendChange(changeRequested)
        out.println(changeBack.joinToString(" "))
    } catch(e: Exception) {
        out.println(e.message)
    }
}
