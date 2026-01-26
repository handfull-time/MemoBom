package com.utime.memoBom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.util.SeedCipherUtil;
import com.utime.memoBom.common.vo.AppDefine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class MemoBomApplication {
	
	/*
	 * java -jar app.jar --spring.config.import=file:/etc/project.properties
	 * 
	 * google client id : https://console.cloud.google.com/
	 * https://console.cloud.google.com/auth/clients/{google.client-id}?project=memobom
	 * 
	 * jdbc:h2:file:${h2.path}/${appName}.h2;AUTO_SERVER=TRUE
	 * 
	 * 
appName=MemoBom
jwt.secret=
pwSaltKey=
h2.username=BomDbAgent
h2.password=
h2.path=
google.client-id=
google.client-secret=
	 */

	
	/**
	 * 암호 키 생성
	 * @param len
	 * @return
	 */
	private static byte [] generateRandomKey(int len) {
		final byte[] iv = new byte[len];
	    new SecureRandom().nextBytes(iv);
	    return new IvParameterSpec(iv).getIV();
	}
	
	/**
	 * 포트가 살아있는지 검사.
	 * @param port 포트 번호
	 * @return true: 살아 있다. false:없다.
	 */
	private static boolean isLivePort(final int port) {
		
		Exception socketException = null;
		try {
			final Socket socket = new Socket();
	        socket.connect(new InetSocketAddress("127.0.0.1", port), 500);
	        socket.close();
	        socketException = null;
        } catch (Exception ex) {
        	socketException = ex;
        }
		
		return socketException == null;
	}
	
	/**
	 * 빈 환경 설정 값 생성.
	 * @param file
	 */
	private static void emptyProperty(final File file) {
		
        final String empty = "";
        
	    final Properties props = new Properties();
	    props.setProperty("appName", "MemoBom");
	    props.setProperty("h2.username", "BomDbAgent");
	    props.setProperty("h2.password", empty);
	    props.setProperty("h2.path", empty);
	    props.setProperty("google.client-id", empty);
	    props.setProperty("google.client-secret", empty);
	    props.setProperty("korean.dataio.key.SpcdeInfoService", empty);
	    
	    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            props.store(writer, "Request for property creation.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save properties file.", e);
        }
	}
	
	/** EC 공개키 → uncompressed point (0x04 + X + Y) */
    private static byte[] encodePublicKey(ECPublicKey publicKey) {
    	final byte[] x = bigIntTo32Bytes(publicKey.getW().getAffineX());
    	final byte[] y = bigIntTo32Bytes(publicKey.getW().getAffineY());

    	final byte[] encoded = new byte[65];
        encoded[0] = 0x04; // uncompressed
        System.arraycopy(x, 0, encoded, 1, 32);
        System.arraycopy(y, 0, encoded, 33, 32);
        return encoded;
    }

    /** BigInteger → 32 bytes */
    private static byte[] bigIntTo32Bytes(BigInteger value) {
        final byte[] src = value.toByteArray();
        final byte[] dst = new byte[32];

        if (src.length > 32) {
            System.arraycopy(src, src.length - 32, dst, 0, 32);
        } else {
            System.arraycopy(src, 0, dst, 32 - src.length, src.length);
        }
        return dst;
    }

    /** 환경 값 누락 검사 */
	private static void checkProperty(final File file) {
	    
	    final Properties props = new Properties();
	    
	    // 1. Read (UTF-8 Reader 사용)
	    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
	        props.load(reader);
	    } catch (IOException e) {
	        throw new RuntimeException("config load failed.", e);
	    }
	    
	    final String exceptionMessage = " property is missing from the configuration.";
	    if( AppUtils.isEmpty( props.getProperty("appName") ) ) {
	    	throw new RuntimeException("'appName'" + exceptionMessage);
	    }
	    if( AppUtils.isEmpty( props.getProperty("h2.username") ) ) {
	    	throw new RuntimeException("'h2.username'" + exceptionMessage);
	    }
	    if( AppUtils.isEmpty( props.getProperty("h2.password") ) ) {
	    	throw new RuntimeException("'h2.password'" + exceptionMessage);
	    }
	    if( AppUtils.isEmpty( props.getProperty("h2.path") ) ) {
	    	throw new RuntimeException("'h2.path'" + exceptionMessage);
	    }
	    if( AppUtils.isEmpty( props.getProperty("google.client-id") ) ) {
	    	throw new RuntimeException("'google.client-id'" + exceptionMessage);
	    }
	    if( AppUtils.isEmpty( props.getProperty("google.client-secret") ) ) {
	    	throw new RuntimeException("'google.client-secret'" + exceptionMessage);
	    }
	    
	    // 2. Check & Insert (로직 동일)
	    final int beforeLen = props.size();
	    
	    if (!props.containsKey(AppDefine.KeySeedKey)) {
	        final byte[] key = generateRandomKey(128);
	        final String value = Base64.getEncoder().encodeToString(key);
	        props.setProperty(AppDefine.KeySeedKey, value);
	    }
	    
	    if (!props.containsKey(AppDefine.KeySeedIV)) {
	        final byte[] key = generateRandomKey(16);
	        final String value = Base64.getEncoder().encodeToString(key);
	        props.setProperty(AppDefine.KeySeedIV, value);
	    }
	    
	    if (!props.containsKey(AppDefine.KeyJwtSecret)) {
	        final byte[] key = generateRandomKey(64);
	        final String value = Base64.getEncoder().encodeToString(key);
	        props.setProperty(AppDefine.KeyJwtSecret, value);
	    }
	    
        if (!props.containsKey(AppDefine.KeyPushPrivate)) {
        	// 
			try {
			    // 1. BouncyCastle 보안 공급자 등록 (P-256 곡선 알고리즘 사용을 위함)
		        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
		            Security.addProvider(new BouncyCastleProvider());
		        }
		        
	            // 2. EC(Elliptic Curve) 키 쌍 생성기 초기화
				final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(nl.martijndwars.webpush.Utils.ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
//				final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
	            // VAPID는 반드시 'secp256r1' (또는 prime256v1) 곡선을 사용해야 함
	            keyPairGenerator.initialize(new ECGenParameterSpec(nl.martijndwars.webpush.Utils.CURVE));
	            // 3. 키 생성
	            final KeyPair keyPair = keyPairGenerator.generateKeyPair();
	            
	            final ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
	            final ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

	            final byte[] publicKeyBytes = encodePublicKey(publicKey);
	            final byte[] privateKeyBytes = bigIntTo32Bytes(privateKey.getS());
	            
	            props.setProperty(AppDefine.KeyPushPublic, Base64.getUrlEncoder().withoutPadding().encodeToString(publicKeyBytes) );
	            props.setProperty(AppDefine.KeyPushPrivate, Base64.getUrlEncoder().withoutPadding().encodeToString(privateKeyBytes) );

			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
				log.error("", e);
			} 
        }


	    // 3. Save (UTF-8 Writer 사용)
	    if (beforeLen < props.size()) {
	        // log.info("config properties update");
	        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
	            // store 메서드는 Writer를 받으면 유니코드 이스케이프 없이 그대로 문자를 씁니다.
	            props.store(writer, "The configuration file has been updated.");
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to save properties file.", e);
	        }
	    }
	}
	
	
	
	public static void main(String[] args) {
		
		final String space = "\n\n\n";
		
		log.info( space + "\t\tStart MemoBom Application!" + space );
		
		final String processId = ManagementFactory.getRuntimeMXBean().getName();
		final String processorId = processId.substring( 0, processId.indexOf("@") );
		log.info( "MemoBom pid : " + processorId);
		
		// argument load
		final SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
		
		// config file param
		if( ! source.containsProperty("spring.config.import") ) {
			throw new RuntimeException("Configuration file option is required. Please add '--spring.config.import={file_path}'.");
		}
		
		String configPath =  source.getProperty("spring.config.import");
		configPath = configPath.substring("file:".length());
		log.info("config file : {}", configPath );
		log.info("port : {}", source.getProperty("port") );
		
		final File config = new File( configPath );
		if( ! config.exists() ) {
			log.error("File not found. {} {}", config.getAbsolutePath(), config.getName() );
			emptyProperty( config );
			return;
		}
		
		checkProperty( config );
		
		// 서비스 오픈 포트 검사
		int port = 0;
		
        if( AppDefine.IsLinux ) {

            port = Integer.valueOf( source.getProperty("port") );
            
            if( port < 1024 ) {
            	log.info("Not a valid service port.");
        		return;
            }

        }else {
			port = 39709;
        }

        if( MemoBomApplication.isLivePort(port) ) {
    		log.info("Service port [" + port + "] is running.");
    		return;
    	}
        
        
		final Properties defaultProrpties = new Properties();
		defaultProrpties.put("server.port", ""+port);

		final SpringApplication application = new SpringApplication(MemoBomApplication.class);
		application.setDefaultProperties( defaultProrpties );
		
		//Initializer 등록: 컨텍스트가 리프레시(빈 생성)되기 전에 실행됨
		application.addInitializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
            @Override
            public void initialize(ConfigurableApplicationContext applicationContext) {
                // 1. 환경 변수(Environment) 가져오기
                final ConfigurableEnvironment env = applicationContext.getEnvironment();

                // 2. 값 추출
                SeedCipherUtil.key = Base64.getDecoder().decode( env.getProperty(AppDefine.KeySeedKey) );
                SeedCipherUtil.iv = Base64.getDecoder().decode( env.getProperty(AppDefine.KeySeedIV) );
                
                log.info(">> [System-Init] Static Constants Loaded Successfully.");
            }
        });
        
		application.run(args);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("The program is shutting down.");
        }));
	}
}
