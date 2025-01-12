/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.tuple;

import org.hibernate.dialect.Dialect;

import java.io.Serializable;

/**
 * Describes the generation of values of a certain field or property of an entity. A generated
 * value might be generated in Java, or by the database.
 * <ul>
 * <li>Java value generation is the responsibility of an associated {@link ValueGenerator}.
 *     In this case, the generated value is written to the database just like any other field
 *     or property value.
 * <li>A value generated by the database might be generated implicitly, by a trigger, or using
 *     a {@code default} column value specified in DDL, for example, or it might be generated
 *     by a SQL expression occurring explicitly in the SQL {@code insert} or {@code update}
 *     statement. In this case, the generated value is retrieved from the database using a SQL
 *     {@code select}.
 * </ul>
 *
 * @see org.hibernate.annotations.ValueGenerationType
 * @see org.hibernate.annotations.Generated
 * @see org.hibernate.annotations.GeneratorType
 *
 * @author Steve Ebersole
 */
public interface ValueGeneration extends Serializable {
	/**
	 * Specifies that the property value is generated:
	 * <ul>
	 * <li>{@linkplain GenerationTiming#INSERT when the entity is inserted},
	 * <li>{@linkplain GenerationTiming#UPDATE when the entity is updated},
	 * <li>{@linkplain GenerationTiming#ALWAYS whenever the entity is inserted or updated}, or
	 * <li>{@linkplain GenerationTiming#NEVER never}.
	 * </ul>
	 *
	 * @return The {@link GenerationTiming} specifying when the value is generated.
	 */
	GenerationTiming getGenerationTiming();

	/**
	 * Obtain the {@linkplain ValueGenerator Java value generator}, if the value is generated in
	 * Java, or return {@code null} if the value is generated by the database.
	 *
	 * @return The value generator
	 */
	ValueGenerator<?> getValueGenerator();

	/**
	 * Determines if the column whose value is generated is included in the column list of the
	 * SQL {@code insert} or {@code update} statement, in the case where the value is generated
	 * by the database. For example, this method should return:
	 * <ul>
	 * <li>{@code true} if the value is generated by calling a SQL function like
	 *     {@code current_timestamp}, or
	 * <li>{@code false} if the value is generated by a trigger,
	 *     by {@link org.hibernate.annotations.GeneratedColumn generated always as}, or
	 *     using a {@linkplain org.hibernate.annotations.ColumnDefault column default value}.
	 * </ul>
	 * If the value is generated in Java, this method is not called, and so for backward
	 * compatibility with Hibernate 5 it is permitted to return any value. On the other hand,
	 * when a property value is generated in Java, the column certainly must be included in the
	 * column list, and so it's most correct for this method to return {@code true}!
	 *
	 * @return {@code true} if the column is included in the column list of the SQL statement.
	 */
	boolean referenceColumnInSql();

	/**
	 * A SQL expression indicating how to calculate the generated value when the property value
	 * is {@linkplain #generatedByDatabase() generated in the database} and the mapped column is
	 * {@linkplain #referenceColumnInSql() included in the SQL statement}. The SQL expression
	 * might be:
	 * <ul>
	 * <li>a function call like {@code current_timestamp} or {@code nextval('mysequence')}, or
	 * <li>a syntactic marker like {@code default}.
	 * </ul>
	 * When the property value is generated in Java, this method is not called, and its value is
	 * implicitly the string {@code "?"}, that is, a JDBC parameter to which the generated value
	 * is bound.
	 *
	 * @return The column value to be used in the generated SQL statement.
	 */
	String getDatabaseGeneratedReferencedColumnValue();

	/**
	 * A SQL expression indicating how to calculate the generated value when the property value
	 * is {@linkplain #generatedByDatabase() generated in the database} and the mapped column is
	 * {@linkplain #referenceColumnInSql() included in the SQL statement}. The SQL expression
	 * might be:
	 * <ul>
	 * <li>a function call like {@code current_timestamp} or {@code nextval('mysequence')}, or
	 * <li>a syntactic marker like {@code default}.
	 * </ul>
	 * When the property value is generated in Java, this method is not called, and its value is
	 * implicitly the string {@code "?"}, that is, a JDBC parameter to which the generated value
	 * is bound.
	 *
	 * @param dialect The {@linkplain Dialect SQL dialect}, allowing generation of an expression
	 *                in dialect-specific SQL.
	 * @return The column value to be used in the generated SQL statement.
	 */
	default String getDatabaseGeneratedReferencedColumnValue(Dialect dialect) {
		return getDatabaseGeneratedReferencedColumnValue();
	}

	/**
	 * Determines if the property value is generated in Java, or by the database.
	 * <p>
	 * This default implementation returns true if the {@linkplain #getValueGenerator() Java
	 * value generator} is {@code null}.
	 *
	 * @return {@code true} if the value is generated by the database, or false if it is
	 *         generated in Java using a {@link ValueGenerator}.
	 */
	default boolean generatedByDatabase() {
		return getValueGenerator() == null;
	}

	/**
	 * Determines if the property value is written to JDBC as the argument of a JDBC {@code ?}
	 * parameter. This is the case when either:
	 * <ul>
	 * <li>the value is generated in Java, or
	 * <li>{@link #referenceColumnInSql()} is {@code true} and
	 *     {@link #getDatabaseGeneratedReferencedColumnValue()} returns {@code null}.
	 * </ul>
	 *
	 * @see org.hibernate.annotations.Generated#writable()
	 */
	default boolean writePropertyValue() {
		return !generatedByDatabase() // value generated in memory and then written as normal
			// current value of property of entity instance written completely as normal
			|| referenceColumnInSql() && getDatabaseGeneratedReferencedColumnValue()==null;
	}
}
