package tools.test.mongodb;

import java.util.LinkedHashMap;

import org.junit.Assert;

import org.junit.Test;

import tools.mongodb.builder.QueryBuilder;
import tools.mongodb.builder.UpdateBuilder;

/**
 * http://www.mongodb.org/display/DOCS/Advanced+Queries
 * 
 * @author liusan.dyf
 */
public class BuilderTest {

	@Test
	public void allTest() {
		QueryBuilder builder = new QueryBuilder();
		builder.all("key", new Object[] { 1, 2, 3 });

		print(builder);
		Assert.assertEquals("{ \"key\" : { \"$all\" : [ 1 , 2 , 3]}}", builder.toString());
	}

	@Test
	public void eqTest() {
		QueryBuilder builder = new QueryBuilder();
		builder.eq("key", 1).objectId("xxx");

		print(builder);
		Assert.assertEquals("{ \"key\" : 1 , \"_id\" : \"xxx\"}", builder.toString());
	}

	@Test
	public void allChainTest() {
		QueryBuilder builder = new QueryBuilder();
		builder.all("key", new Object[] { 1, 2, 3 });
		builder.all("key", new Object[] { 3, 4, 5 });

		print(builder);
		Assert.assertEquals("{ \"key\" : { \"$all\" : [ 1 , 2 , 3 , 3 , 4 , 5]}}", builder.toString());
	}

	@Test
	public void elemMatchTest() {
		QueryBuilder builder = new QueryBuilder();

		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("a", 1);
		builder.elemMatch("key", map);

		print(builder);
		Assert.assertEquals("{ \"key\" : { \"$elemMatch\" : { \"a\" : 1}}}", builder.toString());
	}

	@Test
	public void existsTest() {
		QueryBuilder builder = new QueryBuilder();

		builder.exists("key", false);
		builder.exists("key", true); // 覆盖

		print(builder);
		Assert.assertEquals("{ \"key\" : { \"$exists\" : true}}", builder.toString());
	}

	@Test
	public void gtTest() {
		QueryBuilder builder = new QueryBuilder();

		builder.gt("key", 4);
		builder.gt("key", 5);
		builder.lt("key", 10);

		print(builder);
		Assert.assertEquals("{ \"key\" : { \"$gt\" : 5 , \"$lt\" : 10}}", builder.toString());
	}

	@Test
	public void orTest() {
		QueryBuilder builder = new QueryBuilder();

		builder.or(new QueryBuilder[] { new QueryBuilder().eq("a", 1), new QueryBuilder().eq("b", 2), });

		print(builder);
		Assert.assertEquals("{ \"$or\" : [ { \"a\" : 1} , { \"b\" : 2}]}", builder.toString());
	}

	@Test
	public void andTest() {
		QueryBuilder builder = new QueryBuilder();

		builder.and(new QueryBuilder[] { new QueryBuilder().eq("a", 1).gt("a", 3).gt("a", 4),
				new QueryBuilder().eq("a", 1).eq("b", 3).eq("c", 4), new QueryBuilder().eq("b", 2), });

		print(builder);
		Assert.assertEquals(
				"{ \"$and\" : [ { \"a\" : { \"$gt\" : 4}} , { \"a\" : 1 , \"b\" : 3 , \"c\" : 4} , { \"b\" : 2}]}",
				builder.toString());
	}

	@Test
	public void gteTest() {
		QueryBuilder builder = new QueryBuilder();

		builder.gte("key", 5);

		print(builder);
		Assert.assertEquals("{ \"key\" : { \"$gte\" : 5}}", builder.toString());
	}

	@Test
	public void inTest() {
		QueryBuilder builder = new QueryBuilder();

		builder.in("key", new Object[] { 1, 2, 3 });

		print(builder);
		Assert.assertEquals("{ \"key\" : { \"$in\" : [ 1 , 2 , 3]}}", builder.toString());
	}

	@Test
	public void ltTest() {
		QueryBuilder builder = new QueryBuilder();

		builder.lt("key", 5);

		print(builder);
		Assert.assertEquals("{ \"key\" : { \"$lt\" : 5}}", builder.toString());
	}

	@Test
	public void modTest() {
		QueryBuilder builder = new QueryBuilder();

		builder.mod("key", 10, 1);// this.a % 10 == 1

		print(builder);
		Assert.assertEquals("{ \"key\" : { \"$mod\" : [ 10 , 1]}}", builder.toString());
	}

	@Test
	public void incTest() {
		UpdateBuilder builder = new UpdateBuilder();

		builder.inc("a", 1).inc("b", 1);

		print("---------------------------");
		print(builder);
		Assert.assertEquals("{ \"$inc\" : { \"a\" : 1.0 , \"b\" : 1.0}}", builder.toString());
	}

	@Test
	public void setTest() {
		UpdateBuilder builder = new UpdateBuilder();

		builder.set("a", 1).set("b", 1);

		print(builder);
		Assert.assertEquals("{ \"$set\" : { \"a\" : 1 , \"b\" : 1}}", builder.toString());
	}

	@Test
	public void unsetTest() {
		UpdateBuilder builder = new UpdateBuilder();

		builder.unset("a").set("b", 1).unset("b");

		print(builder);
		Assert.assertEquals("{ \"$unset\" : { \"a\" : 1 , \"b\" : 1} , \"$set\" : { \"b\" : 1}}", builder.toString());
	}

	@Test
	public void pushAllTest() {
		UpdateBuilder builder = new UpdateBuilder();

		builder.pushAll("a", new Object[] { 4, 5 }).pushAll("a", new Object[] { 1, 2, 3 }).set("b", 1).set("b", 10)
				.unset("b");

		print(builder);
		Assert.assertEquals(
				"{ \"$pushAll\" : { \"a\" : [ 4 , 5 , 1 , 2 , 3]} , \"$set\" : { \"b\" : 10} , \"$unset\" : { \"b\" : 1}}",
				builder.toString());
	}

	@Test
	public void bitOrTest() {
		UpdateBuilder builder = new UpdateBuilder();

		builder.bitOr("a", 1).bitAnd("a", 2).bitOr("b", 1);

		print(builder);
		Assert.assertEquals("{ \"$bit\" : { \"a\" : { \"or\" : 1 , \"and\" : 2}} , \"b\" : { \"or\" : 1}}",
				builder.toString());
	}

	@Test
	public void pullTest() {
		UpdateBuilder builder = new UpdateBuilder();

		QueryBuilder q = new QueryBuilder();
		// q.eq("a", 3);
		q.lt("a", 3);

		builder.pull(q);

		print(builder);
		Assert.assertEquals("{ \"$pull\" : { \"a\" : { \"$lt\" : 3}}}", builder.toString());
	}

	void print(Object obj) {
		System.out.println(obj);
	}
}
