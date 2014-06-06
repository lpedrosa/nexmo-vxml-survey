package com.lpedrosa.common.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Try<T> {

        private final T value;
        private final Optional<Throwable> error;

        public static <T> Try<T> of(ThrowableSupplier<T> supplier) {
            T computationValue = null;
            Optional<Throwable> errorCase = Optional.empty();

            try {
                computationValue = supplier.get();
            } catch (Throwable t) {
                errorCase = Optional.of(t);
            }

            return new Try<>(computationValue, errorCase);
        }

        public static <T> Try<T> success(T value) {
            Objects.requireNonNull(value);

            return new Try<>(value, Optional.empty());
        }

        public static Try<?> failure(Throwable t) {
            Objects.requireNonNull(t);

            return new Try<>(null, Optional.of(t));
        }

        public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
           Objects.requireNonNull(mapper);

           if(this.error.isPresent()) {
               @SuppressWarnings("unchecked")
               Try<U> propagateError = (Try<U>)this;
               return propagateError;
           } else {
               return Try.of(() -> mapper.apply(this.value));
           }
        }

        public <U> Try<U> flatMap(Function<? super T, Try<U>> mapper) {
           Objects.requireNonNull(mapper);

           if(this.error.isPresent()) {
               @SuppressWarnings("unchecked")
               Try<U> propagateError = (Try<U>)this;
               return propagateError;
           } else {
               return mapper.apply(this.value);
           }
        }

        public Try<T> recover(Function<Throwable, T> recoverFunc) {
           Objects.requireNonNull(recoverFunc);

           Optional<Try<T>> recoverFunctionIfError = this.error
                                                         .map(throwable -> lazyApply(throwable, recoverFunc))
                                                         .map(Try::of);

           Try<T> result = recoverFunctionIfError
                           .orElse(this); // is success

           return result;
        }

        public T get() throws Throwable {
            if(this.error.isPresent()) {
                throw this.error.get();
            }
            return value;
        }

        public T orElse(T other) {
            return this.error.map(error -> other)
                             .orElse(this.value);
        }

        public T orElseGet(Supplier<T> supplier) {
            return this.error.map(error -> supplier.get())
                             .orElse(this.value);
        }

        public boolean isFailure() {
            return this.error.isPresent();
        }

        private Try(T value, Optional<Throwable> error) {
            this.value = value;
            this.error = error;
        }

        private <U> ThrowableSupplier<U> lazyApply(Throwable t, Function<Throwable, ? extends U> recover) {
            return () -> recover.apply(t);
        }

        @FunctionalInterface
        public interface ThrowableSupplier<T> {
            T get() throws Throwable;
        }
}
