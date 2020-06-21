package org.example.genericcontroller.app.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.genericcontroller.app.book.dto.BookWithPublisherName;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.entity.Publisher;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookCustomRepository {

    private final EntityManager em;

    public void get(int id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BookWithPublisherName> cq = cb
                .createQuery(BookWithPublisherName.class);

        Root<Book> root = cq.from(Book.class);
        Join<Book, Publisher> publisherJoin = root.join("publisher");

        cq.select(cb.construct(
                BookWithPublisherName.class,
                root.get("id"),
                root.get("name"),
                publisherJoin.get("name")));

        ParameterExpression<Integer> paramTitle = cb.parameter(Integer.class);
        cq.where(cb.equal(root.get("id"), paramTitle));

        TypedQuery<BookWithPublisherName> q = em.createQuery(cq);
        q.setParameter(paramTitle, id);
        List<BookWithPublisherName> books = q.getResultList();

        for (BookWithPublisherName b : books) {
            log.info(b.toString());
        }
    }
}
