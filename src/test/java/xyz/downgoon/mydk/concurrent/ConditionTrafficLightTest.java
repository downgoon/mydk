package xyz.downgoon.mydk.concurrent;

public class ConditionTrafficLightTest extends TrafficLightTest {

	@Override
	protected BooleanSignal createInstance() {
		return new ConditionTrafficLight();
	}
}
