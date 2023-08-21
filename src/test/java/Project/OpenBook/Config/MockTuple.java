package Project.OpenBook.Config;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import org.hibernate.query.criteria.internal.path.AbstractPathImpl;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;

import java.util.Arrays;
import java.util.Map;

public class MockTuple implements Tuple {

    private final Map<Expression<?>, Object> valueMap;

    public MockTuple(Map<Expression<?>, Object> valueMap) {
        this.valueMap = valueMap;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(int index, Class<T> type) {
        return null;
    }

    @Override
    public <T> T get(Expression<T> expr) {
        return (T) valueMap.get(expr);
    }

    @Override
    public int size() {
        return valueMap.size();
    }

    @Override
    public Object[] toArray() {
        return valueMap.values().toArray();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Tuple) {
            return Arrays.equals(toArray(), ((Tuple) obj).toArray());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(toArray());
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }
}
