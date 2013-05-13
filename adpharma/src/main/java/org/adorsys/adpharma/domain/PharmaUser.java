package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "PharmaUser", finders = { "findPharmaUsersByUserNameEquals", "findPharmaUsersByUserNameLike", "findPharmaUsersBySaleKeyEquals" })
@RooJson
public class PharmaUser extends AdPharmaBaseEntity {

	private static Date accExp = DateUtils.addYears(new Date(), 50);

	private String userNumber;

	@Enumerated
	private Genre gender;

	@NotNull
	@Column(unique = true)
	private String userName;

	@NotNull
	private String firstName;

	@NotNull
	private String lastName;

	private String fullName;

	private String password;


	private transient String displayRole;

	public String getDisplayRole(){
		return displayRole ;
	}

	public void setDisplayRole( String displayRole){
		this.displayRole  =displayRole;
	}

	@ElementCollection(fetch=FetchType.LAZY)
	private Set<RoleName> roleNames = new HashSet<RoleName>();

	private String phoneNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date accountExpiration = accExp;

	@Value("false")
	private boolean disableLogin;

	@Value("false")
	private boolean accountLocked;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date credentialExpiration = accExp;

	@Override
	protected void internalPrePersist() {
		office = Site.findSite(Long.valueOf(1));
		changePasswordInternal("123");
		makeFullName();
		saleKey = CipMgenerator.generateSaleKey();
	}

	@PostPersist
	public void postPersit() {
		userNumber = NumberGenerator.getNumber("U-", getId(), 4);
	}


	@Override
	protected void internalPostPersit() {
	}

	@Override
	protected void internalPreUpdate() {
		makeFullName();
	}

	private void makeFullName() {
		fullName = getDisplayName();
	}

	public void protectSomeField() {
		PharmaUser user = PharmaUser.findPharmaUser(getId());
		userNumber = user.getUserNumber();
		userName = user.getUserName();
		office = user.getOffice();
		saleKey = user.getSaleKey();
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	public String getDisplayName() {
		return gender.getSalutation() + " " + getFirstName() + " " + getLastName();
	}



	private void changePasswordInternal(String newPassword) {
		this.password = encodePassword(newPassword);
	}

	public void changePassword(String newPassword) {
		changePasswordInternal(newPassword);
		super.merge();
	}

	public static final String PASSWORD_SALT = "ace6b4f53";

	private String adresse;

	private String email;

	@ManyToOne
	private Site office;

	@Value("15")
	@Min(0L)
	@Max(100L)
	private BigDecimal tauxRemise;

	private String saleKey;

	private String encodePassword(String input) {
		Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
		md5PasswordEncoder.setEncodeHashAsBase64(false);
		return md5PasswordEncoder.encodePassword(input, PASSWORD_SALT);
	}

	public boolean checkExistingPasword(String input) {
		return StringUtils.equals(encodePassword(input), password);
	}

	public static void initPharmaUser() {
		if (PharmaUser.countPharmaUsers() <= 0) {
			System.out.println("[ initialisation des comptes utilisateurs ]");
			PharmaUser admin = new PharmaUser();
			admin.setUserName("admin");
			admin.setFirstName("admin");
			admin.setLastName("admin");
			admin.setAccountLocked(Boolean.FALSE);
			admin.setDisableLogin(Boolean.FALSE);
			admin.setGender(Genre.Neutre);
			admin.getRoleNames().add(RoleName.ROLE_ADMIN);
			admin.setPhoneNumber("77166381");
			admin.persist();
			PharmaUser hideUser = new PharmaUser();
			hideUser.setUserName("adorsys");
			hideUser.setFirstName("adorsys");
			hideUser.setLastName("cm");
			hideUser.setAccountLocked(Boolean.FALSE);
			hideUser.setDisableLogin(Boolean.FALSE);
			hideUser.setGender(Genre.Neutre);
			hideUser.getRoleNames().add(RoleName.ROLE_SUPER_ADMIN);
			hideUser.persist();
			PharmaUser vente = new PharmaUser();
			vente.setUserName("vente");
			vente.setFirstName("session");
			vente.setLastName("vente");
			vente.setAccountLocked(Boolean.FALSE);
			vente.setDisableLogin(Boolean.FALSE);
			vente.setGender(Genre.Neutre);
			vente.getRoleNames().add(RoleName.ROLE_OPEN_SALE_SESSION);
			vente.persist();
		}
	}

	public boolean hasAnyRole(Collection<RoleName> roleNames) {
		Set<RoleName> orig = getRoleNames();
		for (RoleName roleName : roleNames) {
			if (orig.contains(roleName)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasAnyRole(RoleName roleName) {
		Set<RoleName> orig = getRoleNames();

		if (orig.contains(roleName)) {
			return true;
		}

		return false;
	}

	public void validate(BindingResult bindingResult) {
		List<PharmaUser> users = PharmaUser.findPharmaUsersByUserNameEquals(getUserName()).getResultList();
		if (!users.isEmpty()) {
			ObjectError errors = new ObjectError("userName", "Un utilisateur Avec ce UserName Existe Deja !");
			bindingResult.addError(errors);
		}
	}

	@Transactional
	public AdPharmaBaseEntity merge() {
		PharmaUser pharmaUser = PharmaUser.findPharmaUser(getId());
		setPassword(pharmaUser.getPassword());
		setUserNumber(pharmaUser.getUserNumber());
		setFootPrint(pharmaUser.getFootPrint());
		return super.merge();
	}

	public String displayName() {
		return getDisplayName();
	}

	public static TypedQuery<PharmaUser> findPharmaUsersByUserNameEquals(String userName) {
		if (userName == null || userName.length() == 0) throw new IllegalArgumentException("The userName argument is required");
		EntityManager em = PharmaUser.entityManager();
		TypedQuery<PharmaUser> q = null;
		q = em.createQuery("SELECT o FROM PharmaUser AS o WHERE o.userName = :userName  AND o.userName != :hideUser ", PharmaUser.class);
		q.setParameter("hideUser", "adorsys");
		q.setParameter("userName", userName);
		return q;
	}

	public static TypedQuery<PharmaUser> findPharmaUsersByUserNameLike(String userName) {
		if (userName == null || userName.length() == 0) throw new IllegalArgumentException("The userName argument is required");
		userName = userName.replace('*', '%');
		/*if (userName.charAt(0) != '%') {
            userName = "%" + userName;
        }*/
		if (userName.charAt(userName.length() - 1) != '%') {
			userName = userName + "%";
		}
		EntityManager em = PharmaUser.entityManager();
		TypedQuery<PharmaUser> q = em.createQuery("SELECT o FROM PharmaUser AS o WHERE LOWER(o.userName) LIKE LOWER(:userName) AND o.userName != :hideUser ORDER BY  o.userName ASC", PharmaUser.class);
		q.setParameter("userName", userName);
		q.setParameter("hideUser", "adorsys");
		return q;
	}

	public static List<PharmaUser> findAllPharmaUsers() {
		return entityManager().createQuery("SELECT o FROM PharmaUser AS o WHERE  o.userName != :hideUser ORDER BY  o.firstName ASC ", PharmaUser.class).setParameter("hideUser", "adorsys").getResultList();
	}

	public static PharmaUser findPharmaUser(Long id) {
		if (id == null) return null;
		return entityManager().find(PharmaUser.class, id);
	}

	public static List<PharmaUser> findPharmaUserEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM PharmaUser  AS o WHERE  o.userName != :hideUser ORDER BY  o.userName ASC", PharmaUser.class).setParameter("hideUser", "adorsys").setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}
}
