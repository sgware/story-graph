/**
 * Data structures for building, pruning, reading, and writing {@link StoryGraph
 * story graphs}, which model an interactive narrative where {@link Node nodes} 
 * are moments in the story, and nodes are connected by {@link TemporalEdge
 * temporal edges} that represent how {@link Action actions} change the world's
 * state and {@link EpistemicEdge epistemic edges} that represent what each
 * {@link Character character} believes.
 * <p>
 * A story graph has the following elements: 
 * <ul>
 * <li>A set of {@link Character characters}, or agents, who form plans, take
 * actions, have beliefs, and can be marked as {@link Character#isPlayer()
 * player} or non-player characters</li>
 * <li>A set of {@link Fluent fluents}, or state variables that are each
 * assigned a {@link Value value} in a {@link Node node}</li>
 * <li>A set of {@link Value values} ({@link NominalValue nominal} and {@link
 * NumericValue numeric}) that can be assigned to fluents</li>
 * <li>A set of {@link Action actions}, or events which can change the state
 * </li>
 * <li>A list of {@link Node nodes} representing moments in the story</li>
 * <li>A set of {@link TemporalEdge temporal edges} that represent when an
 * action can be taken and what new state it leads to</li>
 * <li>A set of {@link EpistemicEdge epistemic edges} that represent what each
 * character believes the state to be, where theory of mind can be nested to an
 * arbitrary depth (e.g. what X believes Y believes X believes, etc.)</li>
 * <li>A list of {@link Explanation explanations} that represent the {@link Plan
 * plans} that characters have at each node</li>
 * </ul>
 * <p>
 * {@link Node Nodes} define a utility value for the story as a whole (called
 * the {@link Node#getUtility() "author's utility"}) and {@link
 * Node#getUtility(Character) for each character}. Utility represents how
 * desirable a state is. Typically, the author and characters want to improve
 * their utility, though this is not required.
 * <p>
 * Story graphs store the nodes and edges that represent an interactive story,
 * but they do not store the mechanics that generated the graph. For example, a
 * node has a {@link NodeEdgeList.Out list of temporal out edges} that define
 * what actions are possible and what new states they lead to, but the graph
 * does not contain information for how or when an action would be available at
 * a node or specifically how an action will change a state.
 * <p>
 * Story graphs store these elements in a series of {@code txt} and {@code csv}
 * files. The format is meant balance ease of reading and space efficiency. This
 * package includes methods to {@link GraphReader read} and {@link GraphWriter
 * write} story graphs in this format to both directories and archive files.
 * 
 * @author Stephen G. Ware
 */
package edu.uky.cs.nil.sg;