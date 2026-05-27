# Story Graph Library (Java)

This is a Java library for creating, modifying, reading, and writing story
graphs used for interactive narratives. The nodes of a story graph are the
possible states of the world. The graph has two kinds of edges: temporal edges
that represent events which change the world state and epistemic edges that
represent character beliefs.

The rules of the story world are not represented in the graph; the graphs
contain only the information necessary to tell an interactive story whose
possible states have already been generated. A temporal edge represents a
state, an action that can occur, and the state that would result after that
action, but the rules for when an action can happen and exactly how it changes
the state are not explicitly represented in the graph. Similarly, epistemic
edges represent what a character believes, but the rules for how and when a
character's beliefs should change are not represented.

These graphs represent deterministic stories. This means the outcome of an
action must be certain. There cannot be several possible outcomes from an
action. Similarly, while characters can have wrong beliefs, they cannot have
uncertain beliefs. They can believe the wrong thing, but they cannot believe one
of several possible things.

## Download and Documentation

This library is written in pure Java with no dependencies. You can
[download the JAR file here](https://github.com/sgware/story-graph/tree/main/build/jar).

The [JavaDoc API is here](https://sgware.github.io/story-graph).

You can download and compile this library from source using
[Maven](http://maven.apache.org/) like this:
```
git clone https://github.com/sgware/story-graph.git
cd story-graph
mvn clean install
```

You can add this library to a Maven project's `pom.xml` file like this:
```
<project>
  ...
  <dependencies>
    <!-- Story Graph Library -->
    <dependency>
      <groupId>edu.uky.cs.nil</groupId>
      <artifactId>story-graph</artifactId>
      <version>1.0.0</version> <!-- use most recent version -->
    </dependency>
  </dependencies>
  ...
</project>
```

## Story Graph Elements

Story graphs have the following elements:
- *Characters*: Agents in the story who have beliefs and goals. A character can
be marked as a player character. For example, Alice and Bob are characters.
- *Fluents*: A fluent is a variable which can be assigned a value. A fluent and
its assigned value represents a single fact about the world. For example,
Alice's location is a fluent. The amount of money Alice has is a fluent.
- *Values*: Values can be assigned to fluents. For example, the possible values
that might be assigned to Alice's location are all the places in the story
world. There are three kinds of values. Java's `null` is allowed. The other two
are:
  - *Numeric Values*: Numbers whose values can be represented as a Java
  `double`.
  - *Nominal Values*: All other kinds of values, which are represented as Java
  `String`s.
- *Actions*: An action is an event which changes the state of the story world.
Each action has a (possibly empty) set of consenting characters which are the
characters who are considered to be taking the action. An action like walking to
a different location would have one consenting character (the one who is
walking). An action like giving an item to another character would have two (the
giver and receiver). An action like stealing an item from another character
would have only the thief as a consenting character; even though the victim may
be a character and may be involved, they are not taking the action. An action
representing an accident or force of nature like an earthquake would have no
consenting characters.
- *Nodes*: A node represents one state of the world at some moment in time. Each
node defines a value for every fluent and a utility value (as a Java `double`)
for every character. There is also a utility value for the story itself, which
is sometimes called the author's utility. Higher utility values are considered
better. Node 0 is usually considered the starting state of the world. It might
specify that Alice is in her house, that Alice has 2 coins, that Alice's utility
is 0, that Bob is at the store, that Bob has 0 coins, that Bob is holding food,
that Bob's utility is 0, and that the author's utility is 0.
- *Temporal Edges*: A temporal edge is a directed edge labeled with an action.
The tail node represents the world state before an action. The label represents
the action that occurs. The head node represents the state after the action. A
temporal edge from the node 0 labeled with the action "Alice walks to the store"
might lead to node 1 where Alice's location is the store. A node cannot have
more than one temporal edge extending from it for the same action. In other
words, there cannot be two different edges from node 0 for the action "Alice
walks to the store." This implies that actions are deterministic; their outcomes
are always certain. A temporal edge may be a loop, meaning it is possible for an
action to cause no change to the state.
- *Epistemic Edges*: An epistemic edge is a directed edge labeled with a
character. The tail node represents a world state and the head node represents
the world state the labeled character believes to be the case. For example, if
Bob does not initially know where Alice is, there might be an epistemic edge
from node 0 to node 2 for Bob, and in node 2 Alice's location is set to `null`.
For node 1 (after Alice walks to the store), there may be an epistemic edge for
Bob looping back to node 1, representing that he now knows Alice is in the
store. A node cannot have more than one epistemic edge extending from it for the
same character. In other words, there cannot be two different edges from node 0
for character Bob. This implies that, while character beliefs can be wrong, they
cannot be uncertain. Bob can wrongly believe Alice's location is `null` but he
cannot believe she is in one of two possible places. An epistemic edge may be a
loop, meaning a character believes the state to be exactly as it is.
- *Explanations*: An explanation is a character plan associated with a node. An
explanation represents a series of actions a character might want to take in
some world state. For example, node 0 might have the explanation "Alice walks to
the store. Alice buys food from Bob. Alice eats the food." A node may have any
number of explanations. Explanations are usually plans for a character to
increase their utility, but this is not required. If the character associated
with an explanation is `null` it represents a plan for the story author and is
usually a plan to increase the author utility, but this is not required.

## Story Graph API

The main object is
[`StoryGraph`](https://sgware.github.io/story-graph/edu/uky/cs/nil/sg/StoryGraph.html)
which can be read from and written to file like this:

```
// Read a story graph from file "input.zip".
StoryGraph sg = StoryGraph.from(new File("input.zip"));
// Write the same story graph to file "output.zip".
sg.write(new File("output.zip"));
```

Operations on large story graphs can take a long time. Methods which might run
a long time accept a
[`Status`](https://sgware.github.io/story-graph/edu/uky/cs/nil/sg/Status.html)
object as an argument which they update to reflect their current progress. The
current status can be seen by calling `Status#toString()`. The
[`Task`](https://sgware.github.io/story-graph/edu/uky/cs/nil/sg/Task.html)
interface can be used to run an operation on a different thread and print
occasional status updates.

```
// By default, the status prints once per second.
Task.run(status -> {
	StoryGraph big = StoryGraph.from(new File("input.zip"), status);
	big.write(new File("output.zip"), status);
});
// This prints every 5 minutes.
Task.run(status -> {
	StoryGraph big = StoryGraph.from(new File("input.zip"), status);
	big.write(new File("output.zip"), status);
}, 5, TimeUnit.MINUTES);
```

Characters, fluents, nominal values, and actions are all
[`Symbol`](https://sgware.github.io/story-graph/edu/uky/cs/nil/sg/Symbol.html)s.
A symbol has both an `int` ID number and a `String` name. The ID numbers are
unique and sequential starting at 0. Names must also be unique. The
`SymbolList#add(String)` method is used to create a new symbol or to return an
existing symbol if one with that name already exists.

```
// Make a new story graph.
StoryGraph sg = new StoryGraph();
// Add the characters Alice and Bob. Note this is not java.lang.Character!
Character alice = sg.characters.add("Alice");
Character bob = sg.characters.add("Bob");
// Print "Alice has ID number 0".
System.out.println(alice + " has ID number " + alice.getID());
// This returns the same Alice character as before; it does not make a new one.
Character repeat = sg.characters.add("Alice");
// Add Alice's location and number of coins as fluents. Any string will work.
sg.fluents.add("location(Alice)");
sg.fluents.add("wealth(Alice)");
// Add two nominal values for the two places.
Value home = sg.values.add("Home");
Value store = sg.values.add("Store");
// Numeric values are singletons that do not belong to the graph.
System.out.println(NumericValue.get(2));
// Add the walk action.
Action walk = sg.actions.add("walk(Alice, Home, Store)");
// Make Alice a consenting character for the walk action.
sg.actions.add(walk, alice);
// Make Alice a player character.
sg.characters.setPlayer(alice, true);
```

Nodes, temporal edges, epistemic edges, and explanations are
[`Numbered`](https://sgware.github.io/story-graph/edu/uky/cs/nil/sg/Numbered.html).
A numbered element has a `long` ID number but no name. Numbered elements are
stored in a
[custom array list](https://sgware.github.io/story-graph/edu/uky/cs/nil/sg/BigArrayList.html)
which can hold just over 1 trillion elements. A graph cannot have more than
2<sup>40</sup> nodes, temporal edges, epistemic edges, or explanations.

```
// Get the fluent for Alice's location.
Fluent location = sg.fluents.get("location(Alice)");
// This version throws an exception if the fluent doesn't exist.
Fluent wealth = sg.fluents.require("wealth(Alice)");
// Make a map of fluents to values.
Map<Fluent, Object> values = new HashMap<>();
values.put(location, home);
values.put(wealth, 2);
// Make a map of characters to utility values. The author is null.
Map<Character, Object> utilities = new HashMap<>();
utilities.put(null, 0);
utilities.put(alice, 0);
utilities.put(bob, 0);
// Make a node for the initial state.
Node n0 = sg.nodes.add(values, utilities);
// Make a temporal edge for the walk action.
values.put(location, store);
Node n1 = sg.nodes.add(values, utilities);
sg.edges.temporal.add(n0, walk, n1);
// Make an epistemic edge for Bob's wrong belief about Alice's location.
values.put(location, null);
Node n2 = sg.nodes.add(values, utilities);
sg.edges.epistemic.add(n0, bob, n2);
// Make an explanation for Alice's plan to go to the store for a meal.
Action buy = sg.actions.add("buy(Alice, Food, Bob, Store)");
Action eat = sg.actions.add("eat(Alice, Food)");
sg.explanations.add(n0, alice, walk, buy, eat);
// Print a node's explanations.
for(Explanation e : n0.explanations) {
	System.out.println(e.character + "'s plan:");
	for(Action a : e)
		System.out.println(a);
}
```

Both symbols and numbered elements have a comment that can be set.

```
alice.setComment("Alice is the main character.");
sg.temporal.get(0).setComment("Alice goes to the store.");
```

A graph's elements can be sorted. Afterwards, their ID numbers will be
reassigned to reflect their new ordering.

```
// Alice has ID number 0.
System.out.println(alice.getID());
// Sort the characters in reverse alphabetic order.
sg.characters.sort((c1, c2) -> c2.name.compareTo(c1.name));
// Now Alice has ID number 1.
System.out.println(alice.getID());
```

Graph elements can be removed, and the whole graph can be pruned. When one
element is removed, it may cause other elements in the graph that use it to be
removed also.

```
// There is 1 epistemic edge so far.
System.out.println(sg.edges.epistemic.size());
// This might take a long time if the graph is big.
Task.run(status -> {
	sg.characters.remove(bob, status);
});
// Because Bob was removed, the epistemic edge for Bob was removed also.
System.out.println(sg.edges.epistemic.size());
```

The ID number of a symbol is returned by `Symbol#hashCode()`. Similarly,
`Numbered#hashCode()` returns a value based on its ID number. Note that some
modifications (i.e. removing a character from the graph) will cause elements to
be renumbered, which will change their hash codes. If you are storing graph
elements in a hash table of some kind, modifying the graph may cause that hash
table to become unreliable.

There are two additional numbered elements that you typically will not interact
with directly:
[State](https://sgware.github.io/story-graph/edu/uky/cs/nil/sg/State.html) and
[Plan](https://sgware.github.io/story-graph/edu/uky/cs/nil/sg/Plan.html). These
are used by nodes and explanations respectively. The graph has the opportunity
to reuse state and plan objects to save space. States and plans do not have
comments.

The full API is [documented here](https://sgware.github.io/story-graph).

## Story Graph File Format

These graphs are stored in a file format that is designed to balance space and
readability. Everything is stored in a series of `txt` and `csv` files that
should be easy for other applications to read and write.

Most files are either `txt` files with one string per line or `csv` files of
numbers. Integers are the ID numbers of graph elements. Numbers and numeric
values always contain a decimal point, even if they are integers. So if you see
`4`, it is referring to a graph element with ID number 4, but if you see `4.0`,
it is a number. The values `NaN`, `Infinity`, and `-Infinity` are also possible
for numbers. An empty line in a `txt` file or an empty column in a `csv` file
represents `null`.

When a string contains a double quote, new line, or carriage return character,
it will be wrapped in double quotes and those characters will be escaped as
`\"`, `\n`, and `\r` respectively.

Story graph files can be stored in two ways: in a directory or in a `zip` file.
The names of the files and their formats are the same for both.

- `characters.txt`: The names of each character, one per line. The first
character listed will have ID number 0. The second will have ID number 1, etc.
- `players.txt`: The names of each character marked as a player, one per line.
- `character_comments.txt`: The comments associated with each character. The
string on line 0 is the comment for character 0, etc.
- `fluents.txt`: The names of each fluent, one per line. The first fluent listed
will have ID number 0, etc.
- `fluent_comments.txt`: The comments for each fluent.
- `values.txt`: The names of each nominal value, one per line. The first value
listed will have ID number 0, etc. Note that numeric values are not stored; they
are singleton objects created via the `NumericValue#get(double)` method.
- `value_comments.txt`: The comments for each nominal value.
- `states.csv`: A comma-separated list of fluent values and utility values. Each
line is a different state. The first line is the state with ID number 0, etc.
The number of columns will be the number of fluents + the number of characters +
1 (for the author utility). The value in the first column is the ID number of
the value assigned to the fluent with ID number 0 or a number if the value is a
numeric value. The value in the second column is the ID number of the value
assigned to the fluent with ID number 1 or a number if the value is a numeric
value, etc. The first column after the fluent values is the author utility. The
first column after the author utility is the utility for the character with ID
number 0, etc. Utility values are numeric values. Numeric values always contain
a decimal point to distinguish them from value indices (e.g. 5 is written
`5.0`). There are three special numeric values `NaN`, `Infinity`, and
`-Infinity`.
- `actions.txt`: The name of each action, one per line. The first action listed
will have ID number 0, etc.
- `consent.csv`: A list of consenting characters for each action. The first line
is the list of consenting characters for the action with ID number 0, etc.
- `action_comments.txt`: The comments for each action.
- `plans.csv`: A list of actions in each plan. The first line is the list of the
ID numbers of the actions in the plan with ID number 0, etc.
- `nodes.csv`: A list of the states used for each node. The first line is the ID
number of the state used for the node with ID number 0, etc.
- `node_comments.txt`: The comments for each node.
- `temporal.csv`: The temporal edges. Each line is 3 columns. The first column
is the ID number of the tail node. The second column is the ID number of the
action that is the edge's label. The third column is the ID number of the head
node. The first line is the temporal edge with ID number 0, etc.
- `temporal_comments.txt`: The comments for each temporal edge.
- `epistemic.csv`: The epistemic edges. Each line is 3 columns. The first column
is the ID number of the tail node. The second column is the ID number of the
character that is the edge's label. The third column is the ID number of the
head node. The first line is the epistemic edge with ID number 0, etc.
- `epistemic_comments.txt`: The comments for each epistemic edge.
- `explanations.csv`: The explanations. Each line is 3 columns. The first column
is the ID number of the node the explanation is associated with. The second
column is the ID number of character the explanation is associated with, or
empty if it is associated with the author. The third column is the ID number of
the explanation's plan.
- `explanation_comments.txt`: The comments for each explanation.
- `meta.csv`: The meta-data, one entry per line. Each line is 2 columns. The
first column is the meta-data key. The second is the value, stored as a string
regardless of its type.

Note: Graph elements should not be stored in the meta-data. They will not be
properly read from or written to file.

## Ownership and License

The Story Graph Library was originally developed by Stephen G. Ware PhD,
Associate Professor of Computer Science at the University of Kentucky in 2025.
Development of this software was sponsored in part by a grant from the US
National Science Foundation, #2145153.

This project is released under the
[General Public License version 3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).
In short, this means you are free to download, use, modify, and redistribute
this software as long as you continue to acknowledge the original copyright
holders and as long as you make the software that you create with this library
freely and publicly available under a similar license.

See the license file for full details. The University of Kentucky retains all
rights not specifically granted.

This license allows you to use this software in commercial projects, but only if
you also release your project under a compatible open source license. If you
want to use this software in a project that is not open source, exceptions can
be granted by the copyright holders. Contact the University of Kentucky Office
of Technology Commercialization at otcinfo@uky.edu to discuss licensing this
software for other kinds of projects.

## Version History

- Version 1.0.0: First public release.

## Citation

Please cite this library like this:

> Stephen G. Ware, "Story Graph Library (Java)," GitHub, 2025.
> https://github.com/sgware/story-graph

BiBTeX entry:

```
@misc{ware2025storygraph,
  author={Ware, Stephen G.},
  title={Story Graph Library (Java)},
  publisher={GitHub},
  year={2025},
  howpublished = {\url{https://github.com/sgware/story-graphs}}
}
```