package com.census.management.config;

import com.census.management.entity.*;
import com.census.management.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final HouseholdRepository householdRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RegionRepository regionRepository,
                           HouseholdRepository householdRepository,
                           PersonRepository personRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.regionRepository = regionRepository;
        this.householdRepository = householdRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already initialized with data. Skipping seed.");
            return;
        }

        logger.info("Initializing Census Management System with default seed data...");

        // 1. Create Regions
        Region r1 = regionRepository.save(new Region("Maharashtra", "Mumbai Suburban", "Andheri"));
        Region r2 = regionRepository.save(new Region("Karnataka", "Bengaluru Urban", "Koramangala"));
        Region r3 = regionRepository.save(new Region("Delhi", "Central Delhi", "Connaught Place"));
        Region r4 = regionRepository.save(new Region("Tamil Nadu", "Chennai", "T. Nagar"));

        // 2. Create Users (Admin & Enumerators)
        // Admin user: admin / admin123
        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        userRepository.save(admin);

        // Enumerator 1 (Mumbai): enum_mumbai / enum123
        User enum1 = new User();
        enum1.setUsername("enum_mumbai");
        enum1.setPasswordHash(passwordEncoder.encode("enum123"));
        enum1.setRole(Role.ENUMERATOR);
        enum1.setRegion(r1);
        enum1.setActive(true);
        userRepository.save(enum1);

        // Enumerator 2 (Bangalore): enum_bangalore / enum123
        User enum2 = new User();
        enum2.setUsername("enum_bangalore");
        enum2.setPasswordHash(passwordEncoder.encode("enum123"));
        enum2.setRole(Role.ENUMERATOR);
        enum2.setRegion(r2);
        enum2.setActive(true);
        userRepository.save(enum2);

        // Enumerator 3 (Delhi - Deactivated demo): enum_delhi / enum123
        User enum3 = new User();
        enum3.setUsername("enum_delhi");
        enum3.setPasswordHash(passwordEncoder.encode("enum123"));
        enum3.setRole(Role.ENUMERATOR);
        enum3.setRegion(r3);
        enum3.setActive(false); // Inactive to test security deactivation
        userRepository.save(enum3);

        // 3. Create Households & Members
        // Household 1 (Mumbai)
        Household h1 = new Household("Flat 402, Sea Green Apts, J.P. Road, Andheri West", r1, "Apartment", true, "Tap Water", "9820011223");
        householdRepository.save(h1);

        Person p1 = new Person("MH-1001-A", "Rajesh Sharma", "Male", LocalDate.of(1980, 5, 14), "Head", "Married", "Graduate", "Employed", "Indian", h1);
        Person p2 = new Person("MH-1002-B", "Sunita Sharma", "Female", LocalDate.of(1983, 8, 22), "Spouse", "Married", "Graduate", "Homemaker", "Indian", h1);
        Person p3 = new Person("MH-1003-C", "Aarav Sharma", "Male", LocalDate.of(2010, 11, 5), "Son", "Single", "Secondary", "Student", "Indian", h1);
        personRepository.saveAll(List.of(p1, p2, p3));

        // Household 2 (Mumbai)
        Household h2 = new Household("House 12, Gulmohar Cross Rd, JVPD Scheme, Andheri", r1, "Pucca", true, "Tap Water", "9821098765");
        householdRepository.save(h2);

        Person p4 = new Person("MH-2001-A", "Vikram Malhotra", "Male", LocalDate.of(1955, 3, 10), "Head", "Widowed", "Post Graduate", "Retired", "Indian", h2);
        Person p5 = new Person("MH-2002-B", "Rohan Malhotra", "Male", LocalDate.of(1986, 7, 19), "Son", "Married", "Doctorate", "Employed", "Indian", h2);
        Person p6 = new Person("MH-2003-C", "Pooja Malhotra", "Female", LocalDate.of(1989, 12, 1), "Daughter-in-Law", "Married", "Post Graduate", "Employed", "Indian", h2);
        personRepository.saveAll(List.of(p4, p5, p6));

        // Household 3 (Bangalore)
        Household h3 = new Household("No. 88, 5th Main, 4th Block, Koramangala", r2, "Pucca", true, "Tap Water", "9448012345");
        householdRepository.save(h3);

        Person p7 = new Person("KA-3001-A", "Anand Rao", "Male", LocalDate.of(1975, 9, 28), "Head", "Married", "Graduate", "Self-Employed", "Indian", h3);
        Person p8 = new Person("KA-3002-B", "Lakshmi Rao", "Female", LocalDate.of(1978, 4, 15), "Spouse", "Married", "Graduate", "Employed", "Indian", h3);
        Person p9 = new Person("KA-3003-C", "Sneha Rao", "Female", LocalDate.of(2005, 6, 12), "Daughter", "Single", "Higher Secondary", "Student", "Indian", h3);
        Person p10 = new Person("KA-3004-D", "Kavita Rao", "Female", LocalDate.of(1948, 1, 20), "Mother", "Widowed", "Illiterate", "Retired", "Indian", h3);
        personRepository.saveAll(List.of(p7, p8, p9, p10));

        // Household 4 (Delhi)
        Household h4 = new Household("Flat 12B, Barakhamba Road, Connaught Place", r3, "Apartment", true, "Tap Water", "9811099887");
        householdRepository.save(h4);

        Person p11 = new Person("DL-4001-A", "Amitabh Sen", "Male", LocalDate.of(1992, 2, 18), "Head", "Single", "Post Graduate", "Employed", "Indian", h4);
        Person p12 = new Person("DL-4002-B", "Debashis Sen", "Male", LocalDate.of(1995, 10, 8), "Brother", "Single", "Graduate", "Unemployed", "Indian", h4);
        personRepository.saveAll(List.of(p11, p12));

        // Household 5 (Chennai)
        Household h5 = new Household("Plot 45, Usman Road, T. Nagar", r4, "Independent House", true, "Tap Water", "9840011223");
        householdRepository.save(h5);

        Person p13 = new Person("TN-5001-A", "Subramanian Swamy", "Male", LocalDate.of(1968, 6, 30), "Head", "Married", "Graduate", "Employed", "Indian", h5);
        Person p14 = new Person("TN-5002-B", "Meenakshi Swamy", "Female", LocalDate.of(1972, 8, 14), "Spouse", "Married", "Higher Secondary", "Homemaker", "Indian", h5);
        Person p15 = new Person("TN-5003-C", "Karthik Swamy", "Male", LocalDate.of(2000, 1, 10), "Son", "Single", "Graduate", "Employed", "Indian", h5);
        personRepository.saveAll(List.of(p13, p14, p15));

        logger.info("Census Management System initialized successfully with 4 regions, 4 users, 5 households, and 15 citizens.");
    }
}
