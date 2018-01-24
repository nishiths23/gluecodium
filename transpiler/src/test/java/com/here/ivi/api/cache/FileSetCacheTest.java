/*
 * Copyright (C) 2018 HERE Global B.V. and its affiliate(s). All rights reserved.
 *
 * This software, including documentation, is protected by copyright controlled by
 * HERE Global B.V. All rights are reserved. Copying, including reproducing, storing,
 * adapting or translating, any or all of this material requires the prior written
 * consent of HERE Global B.V. This material also contains confidential information,
 * which may not be disclosed to others without prior written consent of HERE Global B.V.
 *
 */

package com.here.ivi.api.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import com.here.ivi.api.generator.common.GeneratedFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HashValueCalculator.class)
public final class FileSetCacheTest {

  @Rule public TemporaryFolder testFolder = new TemporaryFolder();

  private static final String RELATIVE_CACHE_PATH = "someDirectory/setA.txt";

  private FileSetCache cache;

  private File cacheFile;

  @Before
  public void setUp() {

    cacheFile = new File(testFolder.getRoot(), RELATIVE_CACHE_PATH);
    cache = new FileSetCache(cacheFile);

    PowerMockito.mockStatic(HashValueCalculator.class);

    //we need valid hash values
    when(HashValueCalculator.calculateHashValue(anyString()))
        .thenAnswer(invocation -> ((String) invocation.getArguments()[0]).getBytes());
  }

  @Test
  public void createEmptyCache() {
    //assert
    assertEquals(new HashMap<String, CacheEntry>(), cache.getCacheEntries());
  }

  @Test
  public void writeEmptyCache() throws IOException, ClassNotFoundException {

    //act
    cache.writeCache();

    //assert
    assertTrue(cacheFile.exists());
    FileInputStream fileInput = new FileInputStream(cacheFile);
    ObjectInputStream objectInputStream = new ObjectInputStream(fileInput);
    HashMap<String, CacheEntry> fileDirectory =
        (HashMap<String, CacheEntry>) objectInputStream.readObject();
    assertEquals(fileDirectory, cache.getCacheEntries());
  }

  @Test
  public void loadEmptyCache() throws IOException, ClassNotFoundException {
    cache.writeCache();

    //act
    FileSetCache loadedCache = new FileSetCache(cacheFile);

    //assert
    assertEquals(cache.getCacheEntries(), loadedCache.getCacheEntries());
  }

  @Test
  public void updateEmptyCache() throws IOException, ClassNotFoundException {

    //act
    List<GeneratedFile> result = cache.updateCache(TestFiles.INITIAL_FILES);

    //assert
    assertTrue(
        cache.getCacheEntries().entrySet().stream().allMatch(entry -> entry.getValue().touched));

    for (GeneratedFile file : TestFiles.INITIAL_FILES) {
      assertTrue(
          cache.getCacheEntries().containsKey(file.targetFile.toPath().normalize().toString()));
      assertTrue(result.contains(file));
    }
    for (GeneratedFile file : TestFiles.INITIAL_FILES) {
      verifyStatic();
      HashValueCalculator.calculateHashValue(file.content);
    }
  }

  @Test
  public void writeNonEmptyCache() throws IOException, ClassNotFoundException {

    cache.updateCache(TestFiles.INITIAL_FILES);

    //act
    cache.writeCache();

    //assert
    assertTrue(cacheFile.exists());
    FileInputStream fileInput = new FileInputStream(cacheFile);
    ObjectInputStream objectInputStream = new ObjectInputStream(fileInput);
    HashMap<String, CacheEntry> fileDirectory =
        (HashMap<String, CacheEntry>) objectInputStream.readObject();
    assertEquals(cache.getCacheEntries(), fileDirectory);
  }

  @Test
  public void loadNonEmptyCache() throws IOException, ClassNotFoundException {

    cache.updateCache(TestFiles.INITIAL_FILES);
    cache.writeCache();

    //act
    FileSetCache loadedCache = new FileSetCache(cacheFile);

    //assert
    Map<String, CacheEntry> loadedEntries = loadedCache.getCacheEntries();
    assertEquals(cache.getCacheEntries(), loadedEntries);
    assertTrue(loadedEntries.entrySet().stream().noneMatch(entry -> entry.getValue().touched));
  }

  @Test
  public void updateNonEmptyCache() {
    //arrange
    cache.updateCache(TestFiles.INITIAL_FILES);
    cache.finalizeUpdates();

    //act (changes: one file removed, one added, one changed, one unchanged)
    List<GeneratedFile> result = cache.updateCache(TestFiles.UPDATED_FILES);

    //assert
    assertEquals(4, cache.getCacheEntries().size());
    List<String> allFilePaths =
        Stream.concat(TestFiles.INITIAL_FILES.stream(), TestFiles.UPDATED_FILES.stream())
            .map(entry -> entry.targetFile.toPath().normalize().toString())
            .collect(Collectors.toList());

    //check that all files are in and have correct touched flag
    for (String path : allFilePaths) {
      CacheEntry value = cache.getCacheEntries().get(path);
      assertNotNull(value);
      if (TestFiles.UPDATED_FILES
          .stream()
          .anyMatch(entry -> entry.targetFile.toPath().normalize().toString().equals(path))) {
        assertTrue(value.touched);
      } else {
        assertFalse(value.touched);
      }
    }

    assertEquals(2, result.size());
    for (GeneratedFile file : TestFiles.CHANGED_FILES) {
      assertTrue(
          result
              .stream()
              .anyMatch(
                  changedFile ->
                      changedFile.targetFile.equals(file.targetFile)
                          && changedFile.content.equals(file.content)));
    }

    Map<String, Long> contentsWithUsageCount =
        Stream.concat(TestFiles.INITIAL_FILES.stream(), TestFiles.UPDATED_FILES.stream())
            .map(file -> file.content)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    for (Map.Entry<String, Long> entry : contentsWithUsageCount.entrySet()) {
      verifyStatic(times((int) (long) entry.getValue()));
      HashValueCalculator.calculateHashValue(entry.getKey());
    }
  }

  @Test
  public void finalizeUpdateTest() {
    //arrange
    CacheEntry first = new CacheEntry("firstHash".getBytes());
    first.touched = false;
    cache.getCacheEntries().put("first", first);
    CacheEntry second = new CacheEntry("secondHash".getBytes());
    second.touched = true;
    cache.getCacheEntries().put("second", second);
    CacheEntry third = new CacheEntry("thirdHash".getBytes());
    third.touched = true;
    cache.getCacheEntries().put("third", third);

    //act
    cache.finalizeUpdates();

    //assert
    assertEquals(2, cache.getCacheEntries().size());
    assertTrue(cache.getCacheEntries().values().stream().allMatch(entry -> !entry.touched));
    CacheEntry secondFromCache = cache.getCacheEntries().get("second");
    CacheEntry thirdFromCache = cache.getCacheEntries().get("third");
    assertEquals(second, secondFromCache);
    assertEquals(third, thirdFromCache);
  }

  @Test
  public void predicateTest() {

    //arrange
    cache.updateCache(TestFiles.UPDATED_FILES);

    //act
    Predicate<Path> checker = cache.filterOutCachedFiles();

    //assert
    assertTrue(checker.test(Paths.get(TestFiles.PATH2)));
    assertTrue(checker.test(Paths.get(RELATIVE_CACHE_PATH)));
    assertFalse(checker.test(Paths.get(TestFiles.PATH3)));
    assertFalse(checker.test(Paths.get(TestFiles.PATH3)));
  }
}
