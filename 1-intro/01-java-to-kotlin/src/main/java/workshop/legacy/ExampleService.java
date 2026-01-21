package workshop.legacy;

import java.util.Objects;

// Tips: CMD+Option+Shift+K konverterer filen til Kotlin
// Men det betyr ikke nødvendigvis at en er ferdig.. :)
public class ExampleService {

    public static class User {
        private String id;
        private String name;

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;
            User user = (User) o;
            return Objects.equals(id, user.id) &&
                   Objects.equals(name, user.name);
        }

        @Override
        public int hashCode() { return Objects.hash(id, name); }

        @Override
        public String toString() {
            return "User{id='" + id + "', name='" + name + "'}";
        }
    }

    public User findUserById(String id) {
        if (id == null) return null;
        return new User(id, "TestUser");
    }
}
