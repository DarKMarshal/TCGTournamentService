package Database.Repositories;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Utility for creating a non-closing wrapper around a shared Connection.
 * <p>
 * In the local (SQLite) profile, all repositories share a single Connection.
 * Repository methods use try-with-resources on the connection returned by getConn(),
 * which would permanently close the shared connection. This wrapper makes close()
 * a no-op so the shared connection stays open for the lifetime of the application.
 * <p>
 * In the azure profile, connections come from a DataSource pool and must be
 * returned (closed) after each use — no wrapping is needed.
 */
final class ConnectionUtil {

    private ConnectionUtil() {}

    static Connection nonClosing(Connection shared) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName())) {
                        return null; // no-op — keep the shared connection open
                    }
                    try {
                        return method.invoke(shared, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
        );
    }
}
