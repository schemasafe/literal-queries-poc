/*
 * Copyright 2016 Tamer AbdulRadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package schemasafe

import schemasafe.core.{Fragment, Materializer, Query}
import singleton.ops.XString

object Example extends App {
  private def q[Q <: XString](q: Q) = Fragment(q)

  final case class ByFoo(foo: Int)
  final case class FooBar(foo: Int, bar: String)

  type testQuery1 = "select foo, bar from table x where foo = ?"
  // Dummy instance, should be derived instead
  implicit val successInstance = Materializer.instance[
    testQuery1,
    ByFoo,
    FooBar,
    Right[Nothing, Query[testQuery1, ByFoo, FooBar, None.type, Array[Byte], Array[Byte], String]]
    ](Right[Nothing, Query[testQuery1, ByFoo, FooBar, None.type, Array[Byte], Array[Byte], String]](new Query[testQuery1, ByFoo, FooBar, None.type, Array[Byte], Array[Byte], String] {
    override def rawQuery = "select foo, bar from table x where foo = ?"
    override def resultSetMaxSize = None
    override def inputEncoder: (ByFoo) => Array[Byte] = _ => Array.empty
    override def outputDecoder: (Array[Byte]) => Either[String, FooBar] = _ => Right(new FooBar(1, "bar"))
  }))

  type testQuery2 = "select foo, bar from table x where foo = ? limit 1;"
  // Dummy instance, should be derived instead
  implicit val successInstance2 = Materializer.instance[
    testQuery2,
    ByFoo,
    FooBar,
    Right[Nothing, Query[testQuery2, ByFoo, FooBar, Some[1L], Array[Byte], Array[Byte], String]]
  ](Right[Nothing, Query[testQuery2, ByFoo, FooBar, Some[1L], Array[Byte], Array[Byte], String]](new Query[testQuery2, ByFoo, FooBar, Some[1L], Array[Byte], Array[Byte], String] {
    override def rawQuery = "select foo, bar from table x where foo = ? limit 1;"
    override def resultSetMaxSize = ??? // Some(1)
    override def inputEncoder: (ByFoo) => Array[Byte] = _ => Array.empty
      override def outputDecoder: (Array[Byte]) => Either[String, FooBar] = _ => Right(new FooBar(1, "bar"))
  }))

  // Dummy instance, should be derived instead
  implicit val failureInstance = Materializer.instance[
  "select oops from table x where foo = ?",
  ByFoo,
  FooBar,
  Left["Column oops not found in table x", Nothing]
  ](Left["Column oops not found in table x", Nothing]("Column oops not found in table x"))

  private val query0 = q("select foo, bar from table x where foo = ?")[ByFoo, FooBar].materialize
  println(query0.inputEncoder(ByFoo(4)))
  val out: FooBar = query0.outputDecoder(Array.empty).right.get
  println(out)

  val selection = q("select foo, bar from table x")
  val clause = q(" where foo = ")
  val query1 = (selection ++ clause :+ "?").apply[ByFoo, FooBar].materialize
  println(query1.inputEncoder(ByFoo(4)))
  val out1: FooBar = query1.outputDecoder(Array.empty).right.get
  println(out1)

  val query2 = q("select foo, bar from table x where foo = ? limit 1;")[ByFoo, FooBar].materialize
  println(query2.resultSetMaxSize.value) // value is member of class Some, we can access it because we statically know it is a Some at compile time
  val size1: 1L = query2.resultSetMaxSize.value // we also know it is exactly one at compile time
  // val size2: 2L = query2.resultSetMaxSize.x // this doesn't compile


  //    val prepared = preparedSync(query0)
  //    val result: Future[Seq[Row]] = executeAsync(prepared) // the monad depends on the underlying driver


  // Fails with: SchemaSafeExample.scala:47: "Column oops not found in table x"
  //  val query2 = q["select oops from table x where foo = ?"][ByFoo, FooBar].materialize

}
