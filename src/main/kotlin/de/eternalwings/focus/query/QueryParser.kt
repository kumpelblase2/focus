package de.eternalwings.focus.query

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.parser.Parser

object QueryParser : Grammar<TaskQuery>() {
    private val at by token("@")
    private val hash by token("#")
    private val openBracket by token("\\{")
    private val closingBracket by token("}")
    private val ws by token("\\s+", ignore = true)
    private val id by token("[\\w/]+")
    private val colon by token(":")

    private val idWithSpaces by separated(id, ws).map { it.terms.joinToString(" ") { term -> term.text } }
    private val context by (at and id).map { TaskQueryPart.ContextPart(it.t2.text) }
    private val bracketContext by (at and openBracket and idWithSpaces and closingBracket).map { TaskQueryPart.ContextPart(it.t3) }
    private val project by (hash and id).map { TaskQueryPart.ProjectPart(it.t1.text) }
    private val bracketProject by (hash and openBracket and idWithSpaces and closingBracket).map { TaskQueryPart.ProjectPart(it.t3) }
    private val shortcut by id.map { TaskQueryPart.ShortcutPart(it.text) }
    private val property by (id and colon and id).map { TaskQueryPart.PropertyPart(it.t1.text, it.t3.text) }
    private val bracketProperty by (id and colon and openBracket and idWithSpaces and closingBracket).map { TaskQueryPart.PropertyPart(it.t1.text, it.t4) }

    private val part = bracketContext or context or bracketProject or project or bracketProperty or property or shortcut

    override val rootParser: Parser<TaskQuery> = zeroOrMore(part).map { TaskQuery(it) }
}
