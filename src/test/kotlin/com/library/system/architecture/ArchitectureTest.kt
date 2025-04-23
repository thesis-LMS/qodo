import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@AnalyzeClasses(packages = ["com.library.system"])
class ArchitectureTest {

//    @ArchTest
//    val layer_dependencies_are_respected: ArchRule = layeredArchitecture()
//        .consideringAllDependencies()
//        .layer("Controller").definedBy("..web..")
//        .layer("Service").definedBy("..services..")
//        .layer("Repository").definedBy("..repository..")
//        .layer("Model").definedBy("..model..")
//
//        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
//        .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
//        .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
//
//    @ArchTest
//    val services_should_be_suffixed: ArchRule = classes()
//        .that().resideInAPackage("..services..")
//        .and().areAnnotatedWith(Service::class.java)
//        .should().haveSimpleNameEndingWith("Service")

    @ArchTest
    val repositories_must_be_interfaces_and_extend_JpaRepository: ArchRule = classes()
        .that().resideInAPackage("..repository..")
        .and().areAnnotatedWith(Repository::class.java)
        .should().beInterfaces()
        .andShould().beAssignableTo(JpaRepository::class.java)


}