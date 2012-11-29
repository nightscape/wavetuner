package org.wavetuner.programs.evaluations

import scala.util.parsing.combinator.RegexParsers
import org.wavetuner.eeg.Measurement

// ocean: alpha.psqllongTermRelativeToMaxPower
// bonus: alpha.relativeToHistory if alpha > alpha.longTerm

case class SingleBandRewarder(rewardName: String, rewardCalculation: String, condition: Option[Comparison]) {
  def toDslString = rewardName + ": " + rewardCalculation + (if (condition.isEmpty) "" else " if " + condition.get.toDslString)
  override def toString = toDslString
}

case class Comparison(left: String, right: String, operator: String) {
  def toDslString = left + operator + right
}

class EvaluationParser extends RegexParsers {
  val simpleValueName: Parser[String] = Measurement.valueNames.map(literal(_)).reduce(_ | _)
  // Sort by length descending to prevent errors from half-parsed extensions
  val valueExtension: Parser[String] = Measurement.valueExtensions.sortBy(_.size).reverse.map(literal(_)).reduce(_ | _)
  val ID: Parser[String] = """[a-zA-Z]([a-zA-Z0-9]|_[a-zA-Z0-9])*"""r
  val valueName: Parser[String] = simpleValueName ~ opt("." ~> valueExtension) ^^ { case svn ~ ext => svn + ext.map("." + _).getOrElse("") }

  val NUM = """[1-9][0-9]*"""r

  def evaluations = evaluation ~ opt(((";" | "\n") ~> evaluation)*) ^^ { case eval ~ optContinued => eval :: optContinued.getOrElse(Nil) }

  def evaluation = ID ~ ":" ~ formula ~ opt("if" ~> condition) ^^
    { case rew ~ ":" ~ form ~ condOpt => SingleBandRewarder(rew, form, condOpt) }

  def formula = valueName

  val comparators = (">=" | "<=" | ">" | "<" | "=")

  def condition = valueName ~ comparators ~ valueName ^^ { case var1 ~ comp ~ var2 => Comparison(var1, var2, comp) }

  def parse(s: String) = parseAll(evaluations, s) match {
    case Success(res, _) => res
    case e => throw new Exception(e.toString)
  }
  def parseOne(s: String) = parseAll(evaluation, s) match {
    case Success(res, _) => res
    case e => throw new Exception(e.toString)
  }
}