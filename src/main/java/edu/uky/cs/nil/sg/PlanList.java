package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * The {@link NumberedList list} of {@link Plan plans} in a {@link StoryGraph
 * story graph}, which as {@link Sequence sequences} of {@link Action actions}.
 * Plans are typically not created directly by the user; they are created when
 * an {@link ExplanationList#add(Node, Character, Plan) explanation is created}.
 * Plan objects exist separately from explanations because they can sometimes be
 * reused by explanations which have the same plan but different nodes or
 * characters.
 * 
 * @author Stephen G. Ware
 */
public class PlanList extends NumberedList<Plan> {
	
	/**
	 * Constructs a new plan list for a given story graph.
	 * 
	 * @param graph the story graph to which this list's plans belong
	 */
	protected PlanList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.PLANS;
	}
	
	@Override
	protected String getSingular() {
		return "plan";
	}
	
	/**
	 * Creates a new plan from an array of {@link Action actions}.
	 * <p>
	 * This method does not check whether a plan with the same actions already
	 * exists; it always adds a new plan.
	 * <p>
	 * If the plan being created now is similar to the most recent plan that was
	 * created last (i.e. the plan whose {@link Plan#getID() ID number} will be
	 * one less than this plan's ID number), this method will attempt to {@link
	 * #add(Action, Plan) make a tail plan} to save memory. Specifically, a tail
	 * plan will be created if the most recently created plan is one action
	 * shorter than this plan and this plan {@link Plan#endsWith(Action...) ends
	 * with} the previous plan, or if the most recently created plan is the same
	 * length as this plan and all actions but the first action are the same as
	 * this plan.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param actions the action in a plan
	 * @return a newly created plan that has been added to this list
	 */
	public Plan add(Action...actions) {
		if(actions == null)
			actions = new Action[0];
		for(Action action : actions)
			graph.actions.require(action);
		if(size() > 0) {
			Plan previous = get(size() - 1);
			if(endsWith(actions, previous))
				return add(actions[0], previous);
			else if(previous instanceof TailPlan tp && endsWith(actions, tp.rest))
				return add(actions[0], tp.rest);
		}
		Plan plan = new ArrayPlan(size(), actions);
		super.add(plan);
		return plan;
	}
	
	private static final boolean endsWith(Action[] actions, Plan plan) {
		if(actions.length != plan.size() + 1)
			return false;
		for(int i = 0; i < plan.size(); i++)
			if(!actions[1 + i].equals(plan.get(i)))
				return false;
		return true;
	}
	
	/**
	 * Creates a new plan from an {@link Iterable iterable} of {@link Action
	 * actions}. See {@link #add(Action...)} for a discussion of when a {@link
	 * TailPlan tail plan} is created.
	 * <p>
	 * This method does not check whether a plan with the same actions already
	 * exists; it always adds a new plan.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param actions the action in a plan
	 * @return a newly created plan that has been added to this list
	 */
	public Plan add(Iterable<Action> actions) {
		return add(Utilities.toArray(actions, Action.class));
	}
	
	/**
	 * Creates a new plan by prepending an {@link Action action} to the start of
	 * an existing {@link Plan plan}. The new plan may be a {@link TailPlan tail
	 * plan} to save memory.
	 * <p>
	 * This method does not check whether a plan with the same actions already
	 * exists; it always adds a new plan.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param action the first action in the plan to be created
	 * @param plan an existing plan to which the action will be prepended
	 * @return a newly created plan that has been added to this list
	 */
	public Plan add(Action action, Plan plan) {
		graph.actions.require(action);
		require(plan);
		TailPlan tail = new TailPlan(size(), action, plan);
		add(tail);
		return tail;
	}
	
	@Override
	public boolean remove(Predicate<? super Plan> predicate, Status status) {
		return removeAndPrune(
			predicate,
			status,
			graph.explanations
		);
	}
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		super.read(reader, status);
		if(reader.setFile(getFileName())) {
			String[] line = reader.readNextLineAsCSV();
			while(line != null) {
				Action[] actions = new Action[line.length];
				for(int i = 0; i < actions.length; i++)
					actions[i] = graph.actions.get(Utilities.toInteger(line[i]));
				add(actions);
				status.increment();
				line = reader.readNextLineAsCSV();
			}
		}
		status.setMessage("Read " + status.getCount() + " " + getPlural());
	}
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		super.write(writer, status);
		for(Plan plan : this) {
			Object[] line = new Object[plan.size()];
			for(int i = 0; i < line.length; i++)
				line[i] = plan.get(i);
			writer.writeNextLineAsCSV(line);
			status.increment();
		}
		status.setMessage("Wrote " + status.getCount() + " " + getPlural());
	}
}