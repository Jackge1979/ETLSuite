package com.cenrise.vfs;

import java.io.IOException;
import java.lang.reflect.Method;

import com.cenrise.variables.VariableSpace;

/**
 * This class supports overriding of config builders by supplying a
 * VariableSpace containing a variable in the format of
 * vfs.[scheme].config.parser where [scheme] is one of the VFS schemes (file,
 * http, sftp, etc...)
 * 
 */
public class KettleFileSystemConfigBuilderFactory {

	/**
	 * This factory returns a FileSystemConfigBuilder. Custom
	 * FileSystemConfigBuilders can be created by implementing the
	 * {@link IKettleFileSystemConfigBuilder} or overriding the
	 * {@link KettleGenericFileSystemConfigBuilder}
	 * 
	 * @see FileSystemConfigBuilder
	 * 
	 * @param varSpace
	 *            A Kettle variable space for resolving VFS config parameters
	 * @param scheme
	 *            The VFS scheme (FILE, HTTP, SFTP, etc...)
	 * @return A FileSystemConfigBuilder that can translate Kettle variables
	 *         into VFS config parameters
	 * @throws IOException
	 */
	public static IKettleFileSystemConfigBuilder getConfigBuilder(
			VariableSpace varSpace, String scheme) throws IOException {
		IKettleFileSystemConfigBuilder result = null;

		// Attempt to load the Config Builder from a variable: vfs.config.parser
		// = class
		String parserClass = varSpace
				.getVariable("vfs." + scheme + ".config.parser"); //$NON-NLS-1$ //$NON-NLS-2$

		if (parserClass != null) {
			try {
				Class<?> configBuilderClass = KettleFileSystemConfigBuilderFactory.class
						.getClassLoader().loadClass(parserClass);
				Method mGetInstance = configBuilderClass
						.getMethod("getInstance"); //$NON-NLS-1$
				if ((mGetInstance != null)
						&& (IKettleFileSystemConfigBuilder.class
								.isAssignableFrom(mGetInstance.getReturnType()))) {
					result = (IKettleFileSystemConfigBuilder) mGetInstance
							.invoke(null);
				} else {
					result = (IKettleFileSystemConfigBuilder) configBuilderClass
							.newInstance();
				}
			} catch (Exception e) {
				// Failed to load custom parser. Throw exception.
				throw new IOException("加载自定义vfs设置解析器失败"); //$NON-NLS-1$
			}
		} else {
			// No custom parser requested, load default
			if (scheme.equalsIgnoreCase("sftp")) { //$NON-NLS-1$
				result = KettleSftpFileSystemConfigBuilder.getInstance();
			} else {
				result = KettleGenericFileSystemConfigBuilder.getInstance();
			}
		}

		return result;
	}

}
