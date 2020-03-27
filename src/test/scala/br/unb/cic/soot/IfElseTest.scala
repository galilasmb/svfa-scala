package br.unb.cic.soot

import br.unb.cic.soot.graph._
import org.scalatest.{BeforeAndAfter, FunSuite}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class IfElseTest extends JSVFATest {
  override def getClassName(): String = "samples.IfElseScenario"

  override def getMainMethod(): String = "main"

  override def analyze(unit: soot.Unit): NodeType = {
    if (unit.isInstanceOf[InvokeStmt]) {
      val invokeStmt = unit.asInstanceOf[InvokeStmt]
      return analyzeInvokeStmt(invokeStmt.getInvokeExpr)
    }
    if (unit.isInstanceOf[soot.jimple.AssignStmt]) {
      val assignStmt = unit.asInstanceOf[AssignStmt]
      if (assignStmt.getRightOp.isInstanceOf[InvokeExpr]) {
        val invokeStmt = assignStmt.getRightOp.asInstanceOf[InvokeExpr]
        return analyzeInvokeStmt(invokeStmt)
      }
    }
    return SimpleNode
  }

  def analyzeInvokeStmt(exp: InvokeExpr): NodeType =
    exp.getMethod.getName match {
      case "source" => SourceNode
      case "sink" => SinkNode
      case _ => SimpleNode
    }
}


class IfElseTestSuite extends FunSuite with BeforeAndAfter {
  var svfa: IfElseTest = _

  before {
    svfa = new IfElseTest()
    svfa.buildSparseValueFlowGraph()
  }

  test("we should correctly compute the number of nodes and edges") {
    assert(svfa.svg.nodes.size == 12)
    assert(svfa.svg.numberOfEdges() == 11)
  }

  test("we should find exactly one conflict in this analysis") {
    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }
}