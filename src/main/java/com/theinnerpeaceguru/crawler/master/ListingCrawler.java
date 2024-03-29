package com.theinnerpeaceguru.crawler.master;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.theinnerpeaceguru.crawler.EntityCSVStorage;
import com.theinnerpeaceguru.crawler.entity.Property;

import ai.preferred.venom.Crawler;
import ai.preferred.venom.Session;
import ai.preferred.venom.SleepScheduler;
import ai.preferred.venom.fetcher.AsyncFetcher;
import ai.preferred.venom.fetcher.Fetcher;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.uagent.UserAgent;
import ai.preferred.venom.validator.EmptyContentValidator;
import ai.preferred.venom.validator.StatusOkValidator;

public class ListingCrawler {

  // Create session keys for CSV printer to print from handler
  static final Session.Key<EntityCSVStorage<Property>> STORAGE_KEY = new Session.Key<>();

  // You can use this to log to console
  private static final Logger LOGGER = LoggerFactory.getLogger(ListingCrawler.class);

  public static void main(String[] args) {

    // Get file to save to
    final String filename = "data/retreatguru.csv";

    // Start CSV printer
    try (EntityCSVStorage<Property> storage = new EntityCSVStorage<>(filename)) {

      // Let's init the session, this allows us to retrieve the array list in the handler
      final Session session = Session.builder()
          .put(STORAGE_KEY, storage)
          .build();

      // Start crawler
      try (Crawler crawler = createCrawler(createFetcher(), session).start()) {
        LOGGER.info("starting crawler...");

        final String startUrl = "https://retreat.guru/search?min=0&max=10000&bt=srch&type=center&page=1";
        crawler.getScheduler().add(new VRequest(startUrl), new ListingHandler());
      } catch (Exception e) {
        LOGGER.error("Could not run crawler: ", e);
      }

    } catch (IOException e) {
      LOGGER.error("unable to open file: {}, {}", filename, e);
    }
  }

  private static Fetcher createFetcher() {
//      ProxyProvider proxyProvider = new ProxyProvider();
    return AsyncFetcher.builder().setUserAgent(new StandardUserAgent())
        .setValidator(
            new EmptyContentValidator(),
            new StatusOkValidator(),
            new ListingValidator())
//        .setProxyProvider(proxyProvider)
        .build();
  }

  private static Crawler createCrawler(Fetcher fetcher, Session session) {
    return Crawler.builder()
        .setFetcher(fetcher)
        .setSession(session)
        // Just to be polite
        .setSleepScheduler(new SleepScheduler(1500, 3000))
        .build();
  }

}
