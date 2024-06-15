import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ByteKey {
    private byte[] key;

    public ByteKey(final byte[] k)
    {
        key = k;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(key);
    }

    @Override
    public boolean equals(Object other)
    {
        if(other == null || getClass() != other.getClass())
        {
            return false;
        }

        ByteKey otherKey = (ByteKey) other;
        return Arrays.equals(key, otherKey.key);
    }

    @Override
    public String toString()
    {
        return new String(key, StandardCharsets.UTF_8);
    }
}
