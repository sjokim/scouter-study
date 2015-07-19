import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class S2PrintMathod {
	public static void main(String[] args) throws IOException {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		ClassReader cr = new ClassReader(getClassBytes());
		cr.accept(new S2MyClassVisitor(cw), 0);
		byte[] newClass = cw.toByteArray();
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

class S2MyClassVisitor extends ClassVisitor implements Opcodes {

	public S2MyClassVisitor(ClassVisitor cv) {
		super(ASM4, cv);
	}

	public void visit(int version, int access, String name, String signature, String superName,
			String[] interfaces) {
		System.out.println(name + " extends " + superName);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		System.out.println("\t" + name + desc);
		return mv;
	}
}
