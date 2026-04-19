package com.cjgr.awandroide

import com.cjgr.awandroide.data.local.UserEntity
import com.cjgr.awandroide.data.repository.UserRepository
import com.cjgr.awandroide.ui.viewmodel.AuthState
import com.cjgr.awandroide.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Pruebas que verifican la carga correcta del usuario en HomeActivity
 * (nombre, foto) y que ProfileActivity recibe los datos actualizados
 * despues de editar el perfil.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeNavigationTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: AuthViewModel

    private val usuarioBase = UserEntity(
        id = 1,
        nombre = "Carlo",
        correo = "carlo@test.com",
        password = "1234",
        saldo = 250.0,
        fotoPerfil = null
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mock()
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── cargarUsuario (datos que muestra HomeActivity en el header) ─────────

    @Test
    fun `cargarUsuario expone nombre y saldo en currentUser`() = runTest {
        whenever(userRepository.buscarPorId(1)).thenReturn(usuarioBase)

        viewModel.cargarUsuario(1)

        val user = viewModel.currentUser.first()
        assertNotNull(user)
        assertEquals("Carlo", user!!.nombre)
        assertEquals(250.0, user.saldo, 0.001)
    }

    @Test
    fun `cargarUsuario con id invalido deja currentUser en null`() = runTest {
        whenever(userRepository.buscarPorId(-1)).thenReturn(null)

        viewModel.cargarUsuario(-1)

        assertNull(viewModel.currentUser.first())
    }

    @Test
    fun `cargarUsuario expone fotoPerfil cuando existe`() = runTest {
        val conFoto = usuarioBase.copy(fotoPerfil = "content://img/1")
        whenever(userRepository.buscarPorId(1)).thenReturn(conFoto)

        viewModel.cargarUsuario(1)

        val user = viewModel.currentUser.first()
        assertEquals("content://img/1", user?.fotoPerfil)
    }

    @Test
    fun `cargarUsuario expone fotoPerfil null cuando no hay foto`() = runTest {
        whenever(userRepository.buscarPorId(1)).thenReturn(usuarioBase)

        viewModel.cargarUsuario(1)

        val user = viewModel.currentUser.first()
        assertNull(user?.fotoPerfil)
    }

    // ── onResume: despues de editar perfil los datos se refrescan ────────────

    @Test
    fun `despues de actualizarPerfil cargarUsuario devuelve nombre nuevo`() = runTest {
        val actualizado = usuarioBase.copy(nombre = "Carlos Nuevo", correo = "nuevo@mail.com")
        whenever(userRepository.buscarPorCorreo("nuevo@mail.com")).thenReturn(null)
        whenever(userRepository.buscarPorId(1)).thenReturn(actualizado)

        viewModel.actualizarPerfil(1, "Carlos Nuevo", "nuevo@mail.com")
        viewModel.cargarUsuario(1)

        val user = viewModel.currentUser.first()
        assertEquals("Carlos Nuevo", user?.nombre)
    }

    @Test
    fun `despues de actualizarFotoPerfil cargarUsuario devuelve nueva URI`() = runTest {
        val conFoto = usuarioBase.copy(fotoPerfil = "content://img/nueva")
        whenever(userRepository.buscarPorId(1)).thenReturn(conFoto)

        viewModel.actualizarFotoPerfil(1, "content://img/nueva")
        viewModel.cargarUsuario(1)

        val user = viewModel.currentUser.first()
        assertEquals("content://img/nueva", user?.fotoPerfil)
    }

    // ── estado del ViewModel al navegar de vuelta a Home ──────────────────

    @Test
    fun `resetState despues de edicion perfil deja estado Idle`() = runTest {
        whenever(userRepository.buscarPorCorreo(any())).thenReturn(null)
        whenever(userRepository.buscarPorId(1)).thenReturn(usuarioBase)

        viewModel.actualizarPerfil(1, "Carlo", "carlo@test.com")
        viewModel.resetState()

        assertTrue(viewModel.authState.first() is AuthState.Idle)
    }
}
