package jedi.option;

import static jedi.option.Options.None;
import static jedi.option.Options.Some;

import java.util.Arrays;

import jedi.functional.Command;
import jedi.functional.Command0;
import jedi.functional.Filter;
import jedi.functional.Functor;
import jedi.functional.Functor0;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class SomeTest extends MockObjectTestCase {

	public void testMatchWithOptionMatcher() {
		Option<Integer> opt = Some(new Integer(1));
		
		opt.match(new OptionMatcher<Integer>() {
			public void caseNone(None<Integer> none) {
				fail();
			}

			public void caseSome(Integer value) {
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public void testMatchWithCommands() {
		Option<String> opt = Some("x");

		Mock someCommand = mock(Command.class);
		Mock noneCommand = mock(Command0.class);
		someCommand.expects(once()).method("execute").with(eq("x"));
		
		opt.match((Command)someCommand.proxy(), (Command0)noneCommand.proxy());
	}
	
	public void testAsList() {
		assertEquals(Arrays.asList("hi"), Some("hi").asList());
	}
	
	@SuppressWarnings("unchecked")
	public void testGetOrElse() {
		Mock generator = mock(Functor0.class);
		generator.expects(never()).method("execute");
		assertTrue(Some(true).getOrElse((Functor0<Boolean>) generator.proxy()));
	}

	@SuppressWarnings("unchecked")
	public void testMap() {
		Mock functor = mock(Functor.class);
		functor.expects(once()).method("execute").with(eq("string")).will(returnValue(true));
		assertEquals(Some(true),Some("string").map((Functor<String, Boolean>) functor.proxy()));
	}
	
	@SuppressWarnings("unchecked")
	public void testForEach() {
		Mock command = mock(Command.class);
		command.expects(once()).method("execute").with(eq("x"));
		Some("x").forEach((Command<String>) command.proxy());
	}
	
	public void testEqualsWhenEqual() {
		assertEquals(Some("a"), Some("a"));
		assertEquals(Some("a").hashCode(), Some("a").hashCode());
	}

	public void testEqualsWhenNotEqual() {
		assertFalse(Some("a").equals("b"));
	}

	@SuppressWarnings("unchecked")
	public void testFilterWhenFilterPasses() {
		Option<String> option = Some("hi");
		Mock f = mock(Filter.class);
		f.expects(once()).method("execute").with(eq("hi")).will(returnValue(true));
		assertSame(option, option.filter((Filter<String>) f.proxy()));
	}

	@SuppressWarnings("unchecked")
	public void testFilterWhenFilterFails() {
		Option<String> option = Some("hi");
		Mock f = mock(Filter.class);
		f.expects(once()).method("execute").with(eq("hi")).will(returnValue(false));
		assertEquals(None(), option.filter((Filter<String>) f.proxy()));
	}
	
	@SuppressWarnings("unchecked")
	public void testMatchWithFunctors() {
		Option<String> option = Some("hi");
		Mock someFunctor = mock(Functor.class);
		Mock noneFunctor = mock(Functor0.class);
		someFunctor.expects(once()).method("execute").with(eq("hi")).will(returnValue(new Integer(1)));
		assertEquals(new Integer(1), option.match((Functor<String, Integer>) someFunctor.proxy(), (Functor0<Integer>) noneFunctor.proxy()));
	}
}
