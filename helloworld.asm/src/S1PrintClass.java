import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class S1PrintClass {
	public static void main(String[] args) throws IOException {
		ClassReader cr = new ClassReader(getClassBytes());
		cr.accept(new ClassVisitor(Opcodes.ASM4) {
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				System.out.println("Name= " + name);
				System.out.println("superName= " + superName);
			}
		}, 0);

	}

	private static byte[] getClassBytes() throws IOException {
		File f = new File("/tmp/HelloWorld.class");
		FileInputStream r = new FileInputStream(f);
		byte[] buff = new byte[(int) f.length()];
		r.read(buff);
		r.close();
		return buff;
	}

}
