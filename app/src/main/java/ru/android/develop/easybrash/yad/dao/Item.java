package ru.android.develop.easybrash.yad.dao;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "ITEMS".
 */
public class Item {

    private Long id;
    /** Not-null value. */
    private String title;
    /** Not-null value. */
    private String identificator;
    private long categoryId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ItemDao myDao;

    private Category category;
    private Long category__resolvedKey;


    public Item() {
    }

    public Item(Long id) {
        this.id = id;
    }

    public Item(Long id, String title, String identificator, long categoryId) {
        this.id = id;
        this.title = title;
        this.identificator = identificator;
        this.categoryId = categoryId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getItemDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getTitle() {
        return title;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Not-null value. */
    public String getIdentificator() {
        return identificator;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setIdentificator(String identificator) {
        this.identificator = identificator;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /** To-one relationship, resolved on first access. */
    public Category getCategory() {
        long __key = this.categoryId;
        if (category__resolvedKey == null || !category__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CategoryDao targetDao = daoSession.getCategoryDao();
            Category categoryNew = targetDao.load(__key);
            synchronized (this) {
                category = categoryNew;
            	category__resolvedKey = __key;
            }
        }
        return category;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new DaoException("To-one property 'categoryId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.category = category;
            categoryId = category.getId();
            category__resolvedKey = categoryId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}