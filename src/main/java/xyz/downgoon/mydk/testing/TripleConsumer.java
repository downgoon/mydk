package xyz.downgoon.mydk.testing;


@FunctionalInterface
public interface TripleConsumer<X, Y, Z> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param x the first input argument
     * @param y the second input argument
     * @param z the third input argument
     */
    void accept(X x, Y y, Z z);

}
