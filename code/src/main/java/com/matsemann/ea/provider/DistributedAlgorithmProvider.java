package com.matsemann.ea.provider;

import com.matsemann.ea.ipc.DistributedNSGAII;
import org.moeaframework.algorithm.ReferencePointNondominatedSortingPopulation;
import org.moeaframework.core.*;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

import java.util.Properties;

public class DistributedAlgorithmProvider extends AlgorithmProvider {

    @Override
    public Algorithm getAlgorithm(String name, Properties properties, Problem problem) {
        TypedProperties typedProperties = new TypedProperties(properties);

        if (name.startsWith("NSGA-IId")) {
            return newNSGAII(typedProperties, problem);
        } else if (name.startsWith("NSGA-IIId")) {
            return newNSGAIII(typedProperties, problem);
        }

        return null;
    }

    private Algorithm newNSGAII(TypedProperties properties, Problem problem) {
        int populationSize = (int)properties.getDouble("populationSize", 100);

        Initialization initialization = new RandomInitialization(problem,
                populationSize);

        NondominatedSortingPopulation population =
                new NondominatedSortingPopulation();

        TournamentSelection selection = new TournamentSelection(2,
                new ChainedComparator(
                        new ParetoDominanceComparator(),
                        new CrowdingComparator()));

        Variation variation = OperatorFactory.getInstance().getVariation(null,
                properties, problem);

        return new DistributedNSGAII(problem, population, null, selection, variation,
                initialization);
    }


    private Algorithm newNSGAIII(TypedProperties properties, Problem problem) {
        int populationSize = (int)properties.getDouble("populationSize", 100);

        Initialization initialization = new RandomInitialization(problem,
                populationSize);

        ReferencePointNondominatedSortingPopulation population = null;

        if (properties.contains("divisionsOuter") && properties.contains("divisionsInner")) {
            int divisionsOuter = (int)properties.getDouble("divisionsOuter", 4);
            int divisionsInner = (int)properties.getDouble("divisionsInner", 0);

            population = new ReferencePointNondominatedSortingPopulation(
                    problem.getNumberOfObjectives(), divisionsOuter,
                    divisionsInner);
        } else {
            int divisions = (int)properties.getDouble("divisions", 4);
//            divisions = 4; // TODO
            population = new ReferencePointNondominatedSortingPopulation(
                    problem.getNumberOfObjectives(), divisions);
        }

        TournamentSelection selection = new TournamentSelection(2,
                new ParetoDominanceComparator());

        Variation variation = OperatorFactory.getInstance().getVariation(null,
                properties, problem);

        return new DistributedNSGAII(problem, population, null, selection, variation,
                initialization);
    }
}