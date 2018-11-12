package com.dev;

import com.dev.pojo.Person;
import com.dev.util.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisGuideApplicationTests {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testOpsValue() throws InterruptedException {
		// 准备数据
		Person person = new Person();
		person.setId(1L);
		person.setAge(18);
		person.setUserName("lily");
		// opsValue
		ValueOperations<String, String> opsValue = redisTemplate.opsForValue();
		opsValue.set("person:" + person.getId(), JsonUtils.serialize(person));
		Thread.currentThread().sleep(10);
		String json = opsValue.get("person:" + person.getId());
		Person p = JsonUtils.parse(json, Person.class);
		System.out.println("p.userName = " + p.getUserName());
		// opsValue with time
		opsValue.set("time:"+person.getId(),JsonUtils.serialize(person),10, TimeUnit.SECONDS);

		//===================另外一种方式： 绑定key=========================//
		BoundValueOperations<String, String> boundOpsValue = redisTemplate.boundValueOps("bound:" + person.getId());
		boundOpsValue.set(JsonUtils.serialize(person));
		String json02 = boundOpsValue.get();
		Person p2 = JsonUtils.parse(json02, Person.class);
		System.out.println("p2.getAge() = " + p2.getAge());
	}

	@Test
	public void testOpsList() {
		// 准备数据
		Person person = new Person();
		person.setId(1L);
		person.setAge(18);
		person.setUserName("lily");
		Person person02 = new Person();
		person02.setId(2L);
		person02.setAge(16);
		person02.setUserName("jack");
		List<Person> listPerson = new ArrayList<>();
		listPerson.add(person);
		listPerson.add(person02);
		// opsList
		ListOperations<String, String> opsList = redisTemplate.opsForList();
		opsList.leftPush("list:left:"+listPerson.size(),
				JsonUtils.serialize(listPerson));
		opsList.rightPush("list:right:"+listPerson.size(),
				JsonUtils.serialize(listPerson));
	}

}
