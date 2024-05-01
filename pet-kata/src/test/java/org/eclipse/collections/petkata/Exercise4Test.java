/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.petkata;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.factory.list.primitive.MutableIntListFactory;
import org.eclipse.collections.api.factory.set.primitive.MutableIntSetFactory;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.block.factory.primitive.IntPredicates;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.lang.model.type.PrimitiveType;

import static java.lang.Math.abs;

/**
 * In this set of tests, wherever you see .stream() replace it with an Eclipse Collections alternative.
 * <p/>
 * {@link org.eclipse.collections.api.list.primitive.MutableIntList}<br>
 * {@link org.eclipse.collections.api.set.primitive.IntSet}<br>
 * {@link org.eclipse.collections.impl.factory.primitive.IntSets}<br>
 * {@link org.eclipse.collections.impl.block.factory.primitive.IntPredicates}<br>
 * {@link org.eclipse.collections.api.bag.MutableBag}<br>
 * {@link org.eclipse.collections.api.list.MutableList}<br>
 * {@link MutableIntListFactory#empty()}<br>
 * {@link MutableIntSetFactory#empty()}<br>
 * {@link MutableList#flatCollectInt(Function, MutableIntCollection)}<br>
 * {@link MutableIntList#toSet()}<br>
 * {@link MutableIntList#summaryStatistics()}<br>
 * {@link MutableIntList#minIfEmpty(int)}<br>
 * {@link MutableIntList#maxIfEmpty(int)}<br>
 * {@link MutableIntList#sum()}<br>
 * {@link MutableIntList#averageIfEmpty(double)}<br>
 * {@link MutableIntList#size()}<br>
 * {@link IntPredicates#greaterThan(int)}<br>
 * {@link MutableIntList#anySatisfy(IntPredicate)}<br>
 * {@link MutableIntList#allSatisfy(IntPredicate)}<br>
 * {@link MutableIntList#noneSatisfy(IntPredicate)}<br>
 *
 * @see <a href="http://eclipse.github.io/eclipse-collections-kata/pet-kata/#/8">Exercise 4 Slides</a>
 */
public class Exercise4Test extends PetDomainForKata {
    @Test
    @Tag("KATA")
    public void getAgeStatisticsOfPets() {
        var petAges = this.people.flatCollect(Person::getPets).collectInt(Pet::getAge);

        // Try to use an IntSet here instead
        var uniqueAges = IntSets.mutable.ofAll(petAges);

        // IntSummaryStatistics is a class in JDK 8 - Look at MutableIntList.summaryStatistics().
        var stats = petAges.summaryStatistics();

        // Is a Set<Integer> equal to an IntSet?
        // Hint: Try IntSets instead of Set as the factory
        var expectedSet = IntSets.mutable.with(1, 2, 3, 4);
        Assertions.assertEquals(expectedSet, uniqueAges);

        // Try to leverage minIfEmpty, maxIfEmpty, sum, averageIfEmpty on IntList
        Assertions.assertEquals(stats.getMin(), petAges.minIfEmpty(0));
        Assertions.assertEquals(stats.getMax(), petAges.maxIfEmpty(0));
        Assertions.assertEquals(stats.getSum(), petAges.sum());
        Assertions.assertEquals(stats.getAverage(), petAges.averageIfEmpty(0));
        Assertions.assertEquals(stats.getCount(), petAges.size());

        // Hint: JDK xyzMatch = Eclipse Collections xyzSatisfy
        Assertions.assertTrue(petAges.allSatisfy(i -> i > 0));
        Assertions.assertFalse(petAges.anySatisfy(i -> i == 0));
        Assertions.assertTrue(petAges.noneSatisfy(i -> i < 0));
    }

    @Test
    @Tag("KATA")
    @DisplayName("bobSmithsPetNamesAsString - 🐱 🐶")
    public void bobSmithsPetNamesAsString() {
        String names = this.people.detectWith(Person::named, "Bob Smith")
                .getPets()
                .collect(Pet::getName)
                .makeString(" & ");

        Assertions.assertEquals("Dolly & Spot", names);
    }

    @Test
    @Tag("KATA")
    public void immutablePetCountsByEmoji() {
        MutableBag<String> countsByEmoji = this.people.flatCollect(Person::getPets).countBy(Pet::toString);

        Map<String, Long> expected = Map.of("🐱", 2L, "🐶", 2L, "🐹", 2L, "🐍", 1L, "🐢", 1L, "🐦", 1L);
        for (Map.Entry<String, Long> entry : expected.entrySet()) {
            Assertions.assertEquals(countsByEmoji.occurrencesOf(entry.getKey()), entry.getValue());
        }
    }

    /**
     * The purpose of this test is to determine the top 3 pet types.
     */
    @Test
    @Tag("KATA")
    @DisplayName("topThreePets - 🐱 🐶 🐹")
    public void topThreePets() {
        var favorites = this.people.flatCollect(Person::getPets).countBy(Pet::getType).topOccurrences(3).sortThis();

        Verify.assertSize(3, favorites);

        Verify.assertContains(PrimitiveTuples.pair(PetType.CAT, 2), favorites);
        Verify.assertContains(PrimitiveTuples.pair(PetType.DOG, 2), favorites);
        Verify.assertContains(PrimitiveTuples.pair(PetType.HAMSTER, 2), favorites);
    }

    @Test
    @Tag("KATA")
    public void getMedianOfPetAges() {
        // Try to use a MutableIntList here instead
        double median = this.people.flatCollect(Person::getPets).collectInt(Pet::getAge).median();
        Assertions.assertEquals(2.0, median, 0.0);
    }
}
