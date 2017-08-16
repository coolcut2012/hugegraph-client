/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.baidu.hugegraph.example;

import java.util.LinkedList;
import java.util.List;

import com.baidu.hugegraph.driver.GraphManager;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.driver.SchemaManager;
import com.baidu.hugegraph.structure.graph.Edge;
import com.baidu.hugegraph.structure.graph.Vertex;
import com.baidu.hugegraph.structure.schema.EdgeLabel;
import com.baidu.hugegraph.structure.schema.VertexLabel;

public class BatchExample {

    public static void main(String[] args) {
        // If connect failed will throw a exception.
        HugeClient hugeClient = HugeClient.open("http://localhost:8080",
                "hugegraph");

        SchemaManager schema = hugeClient.schema();

        schema.propertyKey("name").asText().ifNotExist().create();
        schema.propertyKey("age").asInt().ifNotExist().create();
        schema.propertyKey("lang").asText().ifNotExist().create();
        schema.propertyKey("date").asText().ifNotExist().create();
        schema.propertyKey("price").asInt().ifNotExist().create();

        VertexLabel person = schema.vertexLabel("person")
                .properties("name", "age")
                .primaryKeys("name")
                .ifNotExist()
                .create();

        schema.vertexLabel("person")
                .properties("price")
                .append();

        VertexLabel software = schema.vertexLabel("software")
                .properties("name", "lang", "price")
                .primaryKeys("name")
                .ifNotExist()
                .create();

        schema.indexLabel("personByName")
                .onV("person").by("name")
                .secondary()
                .ifNotExist()
                .create();

        schema.indexLabel("softwareByPrice")
                .onV("software").by("price")
                .search()
                .ifNotExist()
                .create();

        EdgeLabel knows = schema.edgeLabel("knows")
                .link("person", "person")
                .properties("date")
                .ifNotExist()
                .create();

        EdgeLabel created = schema.edgeLabel("created")
                .link("person", "software")
                .properties("date")
                .ifNotExist()
                .create();

        schema.indexLabel("createdByDate")
                .onE("created").by("date")
                .secondary()
                .ifNotExist()
                .create();

        // get schema object by name
        System.out.println(schema.getPropertyKey("name"));
        System.out.println(schema.getVertexLabel("person"));
        System.out.println(schema.getEdgeLabel("knows"));
        System.out.println(schema.getIndexLabel("createdByDate"));

        // list all schema objects
        System.out.println(schema.getPropertyKeys());
        System.out.println(schema.getVertexLabels());
        System.out.println(schema.getEdgeLabels());
        System.out.println(schema.getIndexLabels());

        GraphManager graph = hugeClient.graph();

        Vertex marko = new Vertex("person").property("name", "marko")
                .property("age", 29);
        Vertex vadas = new Vertex("person").property("name", "vadas")
                .property("age", 27);
        Vertex lop = new Vertex("software").property("name", "lop")
                .property("lang", "java").property("price", 328);
        Vertex josh = new Vertex("person").property("name", "josh")
                .property("age", 32);
        Vertex ripple = new Vertex("software").property("name", "ripple")
                .property("lang", "java").property("price", 199);
        Vertex peter = new Vertex("person").property("name", "peter")
                .property("age", 35);

        List<Vertex> vertices = new LinkedList<>();
        vertices.add(marko);
        vertices.add(vadas);
        vertices.add(lop);
        vertices.add(josh);
        vertices.add(ripple);
        vertices.add(peter);

        vertices = graph.addVertices(vertices);
        vertices.forEach(vertex -> System.out.println(vertex));

        Edge markoKnowsVadas = new Edge("knows").source(marko).target(vadas)
                .property("date", "20160110");
        Edge markoKnowsJosh = new Edge("knows").source(marko).target(josh)
                .property("date", "20130220");
        Edge markoCreateLop = new Edge("created").source(marko).target(lop)
                .property("date", "20171210");
        Edge joshCreateRipple = new Edge("created").source(josh).target(ripple)
                .property("date", "20171210");
        Edge joshCreateLop = new Edge("created").source(josh).target(lop)
                .property("date", "20091111");
        Edge peterCreateLop = new Edge("created").source(peter).target(lop)
                .property("date", "20170324");

        List<Edge> edges = new LinkedList<>();
        edges.add(markoKnowsVadas);
        edges.add(markoKnowsJosh);
        edges.add(markoCreateLop);
        edges.add(joshCreateRipple);
        edges.add(joshCreateLop);
        edges.add(peterCreateLop);

        edges = graph.addEdges(edges, false);
        edges.forEach(edge -> System.out.println(edge));

    }

}
