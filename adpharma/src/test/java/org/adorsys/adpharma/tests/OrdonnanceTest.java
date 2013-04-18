package org.adorsys.adpharma.tests;

import java.util.Date;

import junit.framework.Assert;

import org.adorsys.adpharma.domain.Ordonnancier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:META-INF/spring/applicationContext*.xml"})
public class OrdonnanceTest {
	
    private Ordonnancier ordonnance1;
    
    private Ordonnancier ordonnance2;
    
	
	@Before
	public void setUp(){
		ordonnance1= new Ordonnancier();
		ordonnance1.setId(new Long(1));
		ordonnance1.setPrescripteur("Hsimo");
		ordonnance1.setDateSaisie(new Date());
		ordonnance1.setHospital("Deido");
		
		ordonnance2= new Ordonnancier();
		ordonnance2.setId(new Long(2));
		ordonnance2.setPrescripteur("Nono");
		ordonnance2.setDateSaisie(new Date());
		ordonnance2.setHospital("Loum");
	}
	
	@After
	public void tearDown(){
		
	}
	
	@Test
	public void testEqualHospital(){
		String test="Deido";
//		Assert.assertEquals(test, ordonnance2.getHospital());
		Assert.assertEquals("Erreur de test", test, ordonnance2.getHospital());
	}


}
