package id.ac.ui.cs.advprog.hiringgo.log.dto;

import id.ac.ui.cs.advprog.hiringgo.log.dto.response.BaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseResponseTest {

    private BaseResponse<String> baseResponse;

    @BeforeEach
    void setUp() {
        baseResponse = new BaseResponse<>();
    }

    @Test
    void testNoArgsConstructor() {
        BaseResponse<String> response = new BaseResponse<>();
        assertNull(response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testAllArgsConstructor() {
        String expectedMessage = "Success";
        String expectedData = "Test Data";

        BaseResponse<String> response = new BaseResponse<>(expectedMessage, expectedData);

        assertEquals(expectedMessage, response.getMessage());
        assertEquals(expectedData, response.getData());
    }

    @Test
    void testSetAndGetMessage() {
        String expectedMessage = "Test Message";
        baseResponse.setMessage(expectedMessage);
        assertEquals(expectedMessage, baseResponse.getMessage());
    }

    @Test
    void testSetAndGetData() {
        String expectedData = "Test Data";
        baseResponse.setData(expectedData);
        assertEquals(expectedData, baseResponse.getData());
    }

    @Test
    void testWithDifferentGenericTypes() {
        // Test with Integer
        BaseResponse<Integer> intResponse = new BaseResponse<>("Number", 42);
        assertEquals("Number", intResponse.getMessage());
        assertEquals(Integer.valueOf(42), intResponse.getData());

        // Test with Boolean
        BaseResponse<Boolean> boolResponse = new BaseResponse<>("Boolean", true);
        assertEquals("Boolean", boolResponse.getMessage());
        assertEquals(Boolean.TRUE, boolResponse.getData());

        // Test with custom object
        TestObject testObj = new TestObject("test", 123);
        BaseResponse<TestObject> objResponse = new BaseResponse<>("Object", testObj);
        assertEquals("Object", objResponse.getMessage());
        assertEquals(testObj, objResponse.getData());
    }

    @Test
    void testEqualsAndHashCode() {
        BaseResponse<String> response1 = new BaseResponse<>("Success", "Data1");
        BaseResponse<String> response2 = new BaseResponse<>("Success", "Data1");
        BaseResponse<String> response3 = new BaseResponse<>("Error", "Data1");

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        BaseResponse<String> response = new BaseResponse<>("Success", "Test Data");
        String toString = response.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Success"));
        assertTrue(toString.contains("Test Data"));
    }

    @Test
    void testNullValues() {
        BaseResponse<String> response = new BaseResponse<>(null, null);
        assertNull(response.getMessage());
        assertNull(response.getData());

        // Test setting null values
        baseResponse.setMessage(null);
        baseResponse.setData(null);
        assertNull(baseResponse.getMessage());
        assertNull(baseResponse.getData());
    }

    @Test
    void testMutableBehavior() {
        BaseResponse<String> response = new BaseResponse<>("Initial", "InitialData");

        // Modify message
        response.setMessage("Updated");
        assertEquals("Updated", response.getMessage());
        assertEquals("InitialData", response.getData());

        // Modify data
        response.setData("UpdatedData");
        assertEquals("Updated", response.getMessage());
        assertEquals("UpdatedData", response.getData());
    }

    @Test
    void testWithNullGenericType() {
        BaseResponse<Object> response = new BaseResponse<>("Message", null);
        assertEquals("Message", response.getMessage());
        assertNull(response.getData());
    }

    // Helper class for testing with custom objects
    private static class TestObject {
        private String name;
        private int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestObject that = (TestObject) obj;
            return value == that.value && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode() + value;
        }

        @Override
        public String toString() {
            return "TestObject{name='" + name + "', value=" + value + "}";
        }
    }
}