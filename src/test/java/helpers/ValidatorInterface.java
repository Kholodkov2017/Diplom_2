package helpers;

@FunctionalInterface
public interface ValidatorInterface<R, M> {
    void validate(R response, M model);
}
