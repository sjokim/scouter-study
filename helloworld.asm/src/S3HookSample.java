/**
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  
 *  @author skyworker
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class S3HookSample {
	private static final String INFILE = "/tmp/HelloWorld.class";
	private static final String OUTFILE = "/tmp/HelloWorld1.class";

	public static void main(String[] args) throws IOException {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		ClassReader cr = new ClassReader(load());
		cr.accept(new S3MyClassVisitor(cw), 0);
		byte[] newClass = cw.toByteArray();
		save(newClass);
	}

	private static byte[] load() throws IOException {
		File f = new File(INFILE);
		FileInputStream r = new FileInputStream(f);
		byte[] buff = new byte[(int) f.length()];
		r.read(buff);
		r.close();
		return buff;
	}
	private static void save(byte[] newBytes) throws IOException {
		File f = new File(OUTFILE);
		FileOutputStream w = new FileOutputStream(f);
		w.write(newBytes);
		w.close();
	}
	

}

class S3MyClassVisitor extends ClassVisitor implements Opcodes {

	public S3MyClassVisitor(ClassVisitor cv) {
		super(ASM4, cv);
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		System.out.println(name + " to  HelloWorld1");
		cv.visit(version, access, "HelloWorld1", signature, superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		System.out.println("\t" + name + desc);
		if (name.equals("main")) {
			return new S3MyMethodVisitor(access, desc, mv);
		} else {
			return mv;
		}
	}
}

class S3MyMethodVisitor extends LocalVariablesSorter implements Opcodes {
	private static final String CLASSNAME = "HelloWorld";
	private final static String START_METHOD = "start";
	private static final String START_SIGNATURE = "()V";
	private final static String END_METHOD = "end";
	private static final String END_SIGNATURE = "()V";

	public S3MyMethodVisitor(int access, String desc, MethodVisitor mv) {
		super(ASM4, access, desc, mv);
	}

	@Override
	public void visitCode() {
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, CLASSNAME, START_METHOD, START_SIGNATURE, false);
		mv.visitCode();
	}

	@Override
	public void visitInsn(int opcode) {
		if ((opcode >= IRETURN && opcode <= RETURN)) {
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, CLASSNAME, END_METHOD, END_SIGNATURE, false);
		}
		mv.visitInsn(opcode);
	}

}
