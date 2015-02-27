package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;

interface ThreadTextureLoader {
	void setTexture();
}

public class ThreadTexture implements ThreadTextureLoader {
	ThreadTextureData ttData = null;
	Texture texture = null;
	private boolean filtered = false;
	
	public ThreadTexture(String path) {
		ttData = new ThreadTextureData(new FileHandle(path));
	}

	public ThreadTexture(FileHandle file) {
		ttData = new ThreadTextureData(file);
	}

	public ThreadTexture(String path, boolean filtered) {
		this.filtered = filtered;
		ttData = new ThreadTextureData(new FileHandle(path));
	}

	public ThreadTexture(FileHandle file, boolean filtered) {
		this.filtered = filtered;
		ttData = new ThreadTextureData(file);
	}
	

	public void setTexture() {
		if (ttData == null) {
			return;
		}
		texture = new Texture(ttData);
		if (filtered) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public class ThreadTextureData implements TextureData {
	    private boolean isPrepared = false;
	    private Pixmap pixmap;
	    private int width;
	    private int height;
	    private Pixmap.Format format;
		private String path = null;
		private FileHandle file = null;

	    public ThreadTextureData(String path) {
	        this.path = path;
	    }

	    public ThreadTextureData(FileHandle file) {
	        this.file = file;
	    }

	    @Override
	    public TextureDataType getType() {
	        return TextureDataType.Pixmap;
	    }

	    @Override
	    public boolean isPrepared() {
	        return isPrepared;
	    }

	    @Override
	    public void prepare() {
	        if (isPrepared) throw new GdxRuntimeException("Already prepared");
	        if (path == null && file == null) {
	            return;
	        }
	        if (path != null) {
	        	pixmap = new Pixmap(new FileHandle(path));	        	
	        } else {
	        	pixmap = new Pixmap(file);	        	
	        }
	        width = pixmap.getWidth();
	        height = pixmap.getHeight();
	        format = pixmap.getFormat();
	        isPrepared = true;
	    }

	    @Override
	    public Pixmap consumePixmap() {
	        if (!isPrepared) throw new GdxRuntimeException("Call prepare() before calling getPixmap()");
	        isPrepared = false;
	        Pixmap pixmap = this.pixmap;
	        this.pixmap = null;
	        return pixmap;
	    }

	    @Override
	    public boolean disposePixmap() {
	        return true;
	    }

		@Override
		public void consumeCustomData(int target) {

		}

	    public void consumeCompressedData() {
	        throw new GdxRuntimeException("This TextureData implementation does not upload data itself");
	    }

	    @Override
	    public int getWidth() {
	        return width;
	    }

	    @Override
	    public int getHeight() {
	        return height;
	    }

	    @Override
	    public Pixmap.Format getFormat() {
	        return format;
	    }

	    @Override
	    public boolean useMipMaps() {
	        return false;
	    }

	    @Override
	    public boolean isManaged() {
	        return true;
	    }
	}
}
