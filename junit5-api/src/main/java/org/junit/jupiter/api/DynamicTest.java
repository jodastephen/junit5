/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.jupiter.api;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static org.junit.platform.commons.meta.API.Usage.Experimental;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.platform.commons.meta.API;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ToStringBuilder;

/**
 * A {@code DynamicTest} is a test case generated at runtime.
 *
 * <p>It is composed of a {@linkplain #getDisplayName display name} and an
 * {@link #getExecutable Executable}.
 *
 * <p>Instances of {@code DynamicTest} must be generated by factory methods
 * annotated with {@link TestFactory @TestFactory}.
 *
 * <p>Note that dynamic tests are quite different from standard {@link Test @Test}
 * cases since callbacks such as {@link BeforeEach @BeforeEach} and
 * {@link AfterEach @AfterEach} methods are not executed for dynamic tests.
 *
 * @since 5.0
 * @see #dynamicTest(String, Executable)
 * @see #stream(Iterator, Function, Consumer)
 * @see Test
 * @see TestFactory
 * @see Executable
 */
@API(Experimental)
public class DynamicTest {

	/**
	 * Factory for creating a new {@code DynamicTest} for the supplied display
	 * name and executable code block.
	 *
	 * @param displayName the display name for the dynamic test; never
	 * {@code null} or blank
	 * @param executable the executable code block for the dynamic test;
	 * never {@code null}
	 * @see #stream(Iterator, Function, Consumer)
	 */
	public static DynamicTest dynamicTest(String displayName, Executable executable) {
		return new DynamicTest(displayName, executable);
	}

	/**
	 * Generate a stream of dynamic tests based on the supplied generators
	 * and test executor.
	 *
	 * <p>Use this method when the set of dynamic tests is nondeterministic
	 * in nature.
	 *
	 * <p>The supplied {@code inputGenerator} is responsible for generating
	 * input values. A {@link DynamicTest} will be added to the resulting
	 * stream for each dynamically generated input value, using the supplied
	 * {@code displayNameGenerator} and {@code testExecutor}.
	 *
	 * @param inputGenerator an {@code Iterator} that serves as a dynamic
	 * <em>input generator</em>; never {@code null}
	 * @param displayNameGenerator a function that generates a display name
	 * based on an input value; never {@code null}
	 * @param testExecutor a consumer that executes a test based on an
	 * input value; never {@code null}
	 * @param <T> the type of <em>input</em> generated by the {@code inputGenerator}
	 * and used by the {@code displayNameGenerator} and {@code testExecutor}
	 * @return a stream of dynamic tests based on the supplied generators and
	 * executor; never {@code null}
	 * @see #dynamicTest(String, Executable)
	 */
	public static <T> Stream<DynamicTest> stream(Iterator<T> inputGenerator,
			Function<? super T, String> displayNameGenerator, Consumer<? super T> testExecutor) {

		Preconditions.notNull(inputGenerator, "inputGenerator must not be null");
		Preconditions.notNull(displayNameGenerator, "displayNameGenerator must not be null");
		Preconditions.notNull(testExecutor, "testExecutor must not be null");

		// @formatter:off
		return StreamSupport.stream(spliteratorUnknownSize(inputGenerator, ORDERED), false)
				.map(input -> dynamicTest(displayNameGenerator.apply(input), () -> testExecutor.accept(input)));
		// @formatter:on
	}

	private final String displayName;
	private final Executable executable;

	private DynamicTest(String displayName, Executable executable) {
		this.displayName = Preconditions.notBlank(displayName, "displayName must not be null or blank");
		this.executable = Preconditions.notNull(executable, "executable must not be null");
	}

	/**
	 * Get the display name of this {@code DynamicTest}.
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Get the {@code executable} code block associated with this {@code DynamicTest}.
	 */
	public Executable getExecutable() {
		return this.executable;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("displayName", displayName).toString();
	}

}
