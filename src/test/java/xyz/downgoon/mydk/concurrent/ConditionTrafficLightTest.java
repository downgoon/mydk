package xyz.downgoon.mydk.concurrent;

public class ConditionTrafficLightTest extends TrafficLightTest {

	@Override
	protected TrafficLight createInstance() {
		return new ConditionTrafficLight();
	}
}
