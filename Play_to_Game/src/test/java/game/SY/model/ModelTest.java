package game.SY.model;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
		ModelCreationTest.class,
		ModelGameOverTest.class,
		ModelSixPlayerPlayOutTestSimple.class,
		ModelRoundTest.class,})
public class ModelTest {}
